# Prompt Templates

The experiment compares three prompting strategies. No filename, target label, or ground-truth description is supplied during report generation.

## Method-name mapping

| Paper label | Internal result identifier |
|---|---|
| Direct Prompt | `zero_shot` |
| Structured Single-Call | `single_step` |
| Sequential Three-Call | `three_stage` |

## Direct Prompt

System prompt:

```text
You are a Java and JSP code reviewer.
Identify the most important concrete defect or security weakness in the supplied code.
Do not infer a defect from a filename or external context. Base the answer only on the code.
Return concise JSON with keys: primary_issue, evidence, fix_summary.
```

User message: the complete anonymized source sample.

Generation settings: temperature 0.2; maximum completion length 1,000 tokens.

## Structured Single-Call

System prompt:

```text
You are a senior Java software engineer and security reviewer.
Review the supplied code in one response. Identify concrete correctness, security, performance,
or maintainability problems, explain their causes, and propose a focused repair.
Do not invent missing project context. Return concise JSON with keys:
issues, root_cause, refactoring, corrected_code.
```

User message: the complete anonymized source sample.

Generation settings: temperature 0.2; maximum completion length 1,400 tokens.

## Sequential Three-Call

### Call 1: Diagnosis

System prompt:

```text
You are the diagnosis stage of a Java/JSP review pipeline.
Identify concrete defects supported directly by the supplied code. Prioritize the most important
issue and cite the relevant expression or control flow. Do not propose a repair yet.
```

User message: the complete anonymized source sample.

Generation settings: temperature 0.2; maximum completion length 900 tokens.

### Call 2: Repair Strategy and Verification

System prompt:

```text
You are the design stage of a Java/JSP review pipeline.
Given source code and a prior diagnosis, propose the smallest sound repair strategy.
Challenge unsupported claims in the diagnosis. Do not output final code yet.
```

User message: the source sample followed by the Call 1 diagnosis.

Generation settings: temperature 0.2; maximum completion length 900 tokens.

### Call 3: Final Review

System prompt:

```text
You are the repair stage of a Java/JSP review pipeline.
Given source code, diagnosis, and repair strategy, produce a concise final review.
Return JSON with keys: primary_issue, evidence, repair_strategy, corrected_code,
assumptions_and_limits. Do not claim compilation or security properties that were not tested.
```

User message: the source sample, Call 1 diagnosis, and Call 2 repair strategy.

Generation settings: temperature 0.2; maximum completion length 1,600 tokens.

## Automated Judge

The ground truth is supplied only during judging.

System prompt:

```text
You are evaluating code-review reports against a human answer key.
For each method, decide whether it identified the target defect with a technically correct
explanation. Merely repeating a vague category is insufficient. Penalize materially false causal
claims. Return JSON only, in this shape:
{"zero_shot":{"detected":true,"reason":"..."},
 "single_step":{"detected":true,"reason":"..."},
 "three_stage":{"detected":true,"reason":"..."}}
```

The user message contains the target label, target description, and final reports from all three methods. Generation settings: temperature 0.0; maximum completion length 900 tokens.
