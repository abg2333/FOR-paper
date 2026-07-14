import argparse
import csv
import json
import os
import re
import time
from datetime import datetime, timezone
from pathlib import Path

from openai import OpenAI


PROJECT_DIR = Path(__file__).resolve().parent.parent
DATASET_DIR = PROJECT_DIR / "dataset" / "samples"
GROUND_TRUTH_PATH = PROJECT_DIR / "dataset" / "ground_truth.json"
OUTPUT_DIR = PROJECT_DIR / "reproduction_output"
RAW_DIR = OUTPUT_DIR / "raw"
JUDGMENT_DIR = OUTPUT_DIR / "judgments"

BASE_URL = "https://api.deepseek.com"
MODEL_NAME = os.environ.get("DEEPSEEK_MODEL", "deepseek-chat")
JUDGE_MODEL = os.environ.get("DEEPSEEK_JUDGE_MODEL", MODEL_NAME)
TEMPERATURE = 0.2

METHODS = ("zero_shot", "single_step", "three_stage")

ZERO_SHOT_SYSTEM = """You are a Java and JSP code reviewer.
Identify the most important concrete defect or security weakness in the supplied code.
Do not infer a defect from a filename or external context. Base the answer only on the code.
Return concise JSON with keys: primary_issue, evidence, fix_summary."""

SINGLE_STEP_SYSTEM = """You are a senior Java software engineer and security reviewer.
Review the supplied code in one response. Identify concrete correctness, security, performance,
or maintainability problems, explain their causes, and propose a focused repair.
Do not invent missing project context. Return concise JSON with keys:
issues, root_cause, refactoring, corrected_code."""

DIAGNOSIS_SYSTEM = """You are the diagnosis stage of a Java/JSP review pipeline.
Identify concrete defects supported directly by the supplied code. Prioritize the most important
issue and cite the relevant expression or control flow. Do not propose a repair yet."""

ARCHITECTURE_SYSTEM = """You are the design stage of a Java/JSP review pipeline.
Given source code and a prior diagnosis, propose the smallest sound repair strategy.
Challenge unsupported claims in the diagnosis. Do not output final code yet."""

REPAIR_SYSTEM = """You are the repair stage of a Java/JSP review pipeline.
Given source code, diagnosis, and repair strategy, produce a concise final review.
Return JSON with keys: primary_issue, evidence, repair_strategy, corrected_code,
assumptions_and_limits. Do not claim compilation or security properties that were not tested."""

JUDGE_SYSTEM = """You are evaluating code-review reports against a human answer key.
For each method, decide whether it identified the target defect with a technically correct
explanation. Merely repeating a vague category is insufficient. Penalize materially false causal
claims. Return JSON only, in this shape:
{"zero_shot":{"detected":true,"reason":"..."},
 "single_step":{"detected":true,"reason":"..."},
 "three_stage":{"detected":true,"reason":"..."}}"""


def usage_dict(response):
    usage = getattr(response, "usage", None)
    if usage is None:
        return {}
    return {
        "prompt_tokens": getattr(usage, "prompt_tokens", None),
        "completion_tokens": getattr(usage, "completion_tokens", None),
        "total_tokens": getattr(usage, "total_tokens", None),
    }


def call_model(client, model, system_prompt, user_prompt, temperature, max_tokens):
    started = time.perf_counter()
    response = client.chat.completions.create(
        model=model,
        messages=[
            {"role": "system", "content": system_prompt},
            {"role": "user", "content": user_prompt},
        ],
        temperature=temperature,
        max_tokens=max_tokens,
    )
    return {
        "content": response.choices[0].message.content,
        "latency_seconds": round(time.perf_counter() - started, 3),
        "usage": usage_dict(response),
        "model": model,
    }


def generate_zero_shot(client, code):
    return {
        "method": "zero_shot",
        "calls": [call_model(client, MODEL_NAME, ZERO_SHOT_SYSTEM, code, TEMPERATURE, 1000)],
    }


def generate_single_step(client, code):
    return {
        "method": "single_step",
        "calls": [call_model(client, MODEL_NAME, SINGLE_STEP_SYSTEM, code, TEMPERATURE, 1400)],
    }


def generate_three_stage(client, code):
    diagnosis = call_model(client, MODEL_NAME, DIAGNOSIS_SYSTEM, code, TEMPERATURE, 900)
    architecture_input = (
        "SOURCE CODE:\n" + code + "\n\nPRIOR DIAGNOSIS:\n" + diagnosis["content"]
    )
    architecture = call_model(
        client, MODEL_NAME, ARCHITECTURE_SYSTEM, architecture_input, TEMPERATURE, 900
    )
    repair_input = (
        "SOURCE CODE:\n"
        + code
        + "\n\nDIAGNOSIS:\n"
        + diagnosis["content"]
        + "\n\nREPAIR STRATEGY:\n"
        + architecture["content"]
    )
    repair = call_model(client, MODEL_NAME, REPAIR_SYSTEM, repair_input, TEMPERATURE, 1600)
    return {"method": "three_stage", "calls": [diagnosis, architecture, repair]}


def final_content(record):
    return record["calls"][-1]["content"]


def save_json(path, value):
    path.parent.mkdir(parents=True, exist_ok=True)
    temporary = path.with_suffix(path.suffix + ".tmp")
    temporary.write_text(json.dumps(value, ensure_ascii=False, indent=2), encoding="utf-8")
    temporary.replace(path)


def parse_json_object(text):
    cleaned = text.strip()
    cleaned = re.sub(r"^```(?:json)?\s*", "", cleaned, flags=re.IGNORECASE)
    cleaned = re.sub(r"\s*```$", "", cleaned)
    return json.loads(cleaned)


def generate_reports(client, sample_paths):
    generators = {
        "zero_shot": generate_zero_shot,
        "single_step": generate_single_step,
        "three_stage": generate_three_stage,
    }
    for index, sample_path in enumerate(sample_paths, start=1):
        code = sample_path.read_text(encoding="utf-8")
        for method in METHODS:
            output_path = RAW_DIR / f"{sample_path.stem}__{method}.json"
            if output_path.exists():
                print(f"SKIP {index:02d}/{len(sample_paths)} {sample_path.stem} {method}", flush=True)
                continue
            print(f"RUN  {index:02d}/{len(sample_paths)} {sample_path.stem} {method}", flush=True)
            try:
                record = generators[method](client, code)
                record.update(
                    {
                        "sample_id": sample_path.stem,
                        "source_file": sample_path.name,
                        "created_at": datetime.now(timezone.utc).isoformat(),
                        "temperature": TEMPERATURE,
                    }
                )
                save_json(output_path, record)
            except Exception as exc:
                save_json(
                    output_path.with_suffix(".error.json"),
                    {
                        "sample_id": sample_path.stem,
                        "method": method,
                        "error_type": type(exc).__name__,
                        "error": str(exc),
                        "created_at": datetime.now(timezone.utc).isoformat(),
                    },
                )
                print(f"ERROR {sample_path.stem} {method}: {type(exc).__name__}: {exc}", flush=True)
                time.sleep(3)


def judge_reports(client, ground_truth):
    for index, truth in enumerate(ground_truth, start=1):
        sample_id = truth["sample_id"]
        judgment_path = JUDGMENT_DIR / f"{sample_id}.json"
        if judgment_path.exists():
            print(f"JUDGE SKIP {index:02d}/{len(ground_truth)} {sample_id}", flush=True)
            continue

        reports = {}
        missing = []
        for method in METHODS:
            report_path = RAW_DIR / f"{sample_id}__{method}.json"
            if not report_path.exists():
                missing.append(method)
                continue
            reports[method] = final_content(json.loads(report_path.read_text(encoding="utf-8")))
        if missing:
            print(f"JUDGE WAIT {sample_id}: missing {', '.join(missing)}", flush=True)
            continue

        prompt = json.dumps(
            {
                "target_label": truth["label"],
                "target_description": truth["expected"],
                "reports": reports,
            },
            ensure_ascii=False,
        )
        print(f"JUDGE RUN  {index:02d}/{len(ground_truth)} {sample_id}", flush=True)
        try:
            response = call_model(client, JUDGE_MODEL, JUDGE_SYSTEM, prompt, 0.0, 900)
            try:
                decisions = parse_json_object(response["content"])
                parse_error = None
            except Exception as exc:
                decisions = {}
                parse_error = f"{type(exc).__name__}: {exc}"
            save_json(
                judgment_path,
                {
                    "sample_id": sample_id,
                    "truth": truth,
                    "decisions": decisions,
                    "judge_response": response,
                    "parse_error": parse_error,
                    "created_at": datetime.now(timezone.utc).isoformat(),
                },
            )
        except Exception as exc:
            save_json(
                judgment_path.with_suffix(".error.json"),
                {
                    "sample_id": sample_id,
                    "error_type": type(exc).__name__,
                    "error": str(exc),
                    "created_at": datetime.now(timezone.utc).isoformat(),
                },
            )
            print(f"JUDGE ERROR {sample_id}: {type(exc).__name__}: {exc}", flush=True)
            time.sleep(3)


def aggregate(ground_truth):
    rows = []
    for truth in ground_truth:
        path = JUDGMENT_DIR / f"{truth['sample_id']}.json"
        if not path.exists():
            continue
        judgment = json.loads(path.read_text(encoding="utf-8"))
        for method in METHODS:
            decision = judgment.get("decisions", {}).get(method, {})
            raw_path = RAW_DIR / f"{truth['sample_id']}__{method}.json"
            raw = json.loads(raw_path.read_text(encoding="utf-8")) if raw_path.exists() else {}
            calls = raw.get("calls", [])
            rows.append(
                {
                    "sample_id": truth["sample_id"],
                    "track": truth["track"],
                    "label": truth["label"],
                    "context_limited": bool(truth.get("context_limited", False)),
                    "method": method,
                    "detected": decision.get("detected"),
                    "judge_reason": decision.get("reason"),
                    "api_calls": len(calls),
                    "latency_seconds": round(sum(c.get("latency_seconds", 0) for c in calls), 3),
                    "total_tokens": sum((c.get("usage", {}).get("total_tokens") or 0) for c in calls),
                }
            )

    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    csv_path = OUTPUT_DIR / "sample_results.csv"
    if rows:
        with csv_path.open("w", encoding="utf-8-sig", newline="") as f:
            writer = csv.DictWriter(f, fieldnames=list(rows[0].keys()))
            writer.writeheader()
            writer.writerows(rows)

    summary = {
        "generated_at": datetime.now(timezone.utc).isoformat(),
        "generation_model": MODEL_NAME,
        "judge_model": JUDGE_MODEL,
        "temperature": TEMPERATURE,
        "positive_samples": len(ground_truth),
        "methods": {},
        "limitations": [
            "The dataset contains positive samples only, so precision, false-positive rate, F1, and accuracy cannot be computed.",
            "This run has one trial per method and does not measure run-to-run variance.",
            "The automated judge uses a DeepSeek model and should be audited by human reviewers.",
        ],
    }
    for method in METHODS:
        method_rows = [row for row in rows if row["method"] == method]
        core_rows = [row for row in method_rows if not row["context_limited"]]
        detected_all = sum(row["detected"] is True for row in method_rows)
        detected_core = sum(row["detected"] is True for row in core_rows)
        summary["methods"][method] = {
            "evaluated_all": len(method_rows),
            "detected_all": detected_all,
            "target_detection_rate_all": detected_all / len(method_rows) if method_rows else None,
            "evaluated_core": len(core_rows),
            "detected_core": detected_core,
            "target_detection_rate_core": detected_core / len(core_rows) if core_rows else None,
            "total_api_calls": sum(row["api_calls"] for row in method_rows),
            "total_tokens": sum(row["total_tokens"] for row in method_rows),
            "total_latency_seconds": round(sum(row["latency_seconds"] for row in method_rows), 3),
        }
    save_json(OUTPUT_DIR / "summary.json", summary)
    print(json.dumps(summary, ensure_ascii=False, indent=2), flush=True)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--phase", choices=("all", "generate", "judge", "aggregate"), default="all")
    args = parser.parse_args()

    api_key = os.environ.get("DEEPSEEK_API_KEY")
    if args.phase != "aggregate" and not api_key:
        raise RuntimeError("DEEPSEEK_API_KEY is required and must be supplied through the process environment.")

    sample_paths = sorted(path for path in DATASET_DIR.iterdir() if path.suffix.lower() in {".java", ".jsp"})
    ground_truth = json.loads(GROUND_TRUTH_PATH.read_text(encoding="utf-8"))
    if len(sample_paths) != 30 or len(ground_truth) != 30:
        raise RuntimeError("Expected exactly 30 clean samples and 30 ground-truth records.")

    metadata = {
        "started_at": datetime.now(timezone.utc).isoformat(),
        "generation_model": MODEL_NAME,
        "judge_model": JUDGE_MODEL,
        "temperature": TEMPERATURE,
        "sample_count": len(sample_paths),
        "methods": list(METHODS),
        "base_url": BASE_URL,
    }
    save_json(OUTPUT_DIR / "run_metadata.json", metadata)

    client = OpenAI(api_key=api_key, base_url=BASE_URL) if api_key else None
    if args.phase in {"all", "generate"}:
        generate_reports(client, sample_paths)
    if args.phase in {"all", "judge"}:
        judge_reports(client, ground_truth)
    aggregate(ground_truth)


if __name__ == "__main__":
    main()
