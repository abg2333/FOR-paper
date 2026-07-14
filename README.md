# Anonymous Supplementary Artifact

This anonymous artifact accompanies a double-blind submission evaluating direct, structured single-call, and sequential three-call prompting for automated Java/JSP code review.

No author names, affiliations, email addresses, personal repository links, API credentials, or machine-specific absolute paths are included.

## Contents

```text
dataset/
  samples/              30 anonymized Java/JSP samples
  ground_truth.json     target labels and descriptions
  manifest.csv          sample-level index
prompts/
  PROMPTS.md            exact generation and judging prompts
scripts/
  run_experiment.py     resumable experiment and aggregation script
results/
  raw/                  90 generated reports
  judgments/            30 structured automated-judge records
  run_metadata.json     model and execution configuration
  sample_results.csv    per-sample decisions and costs
  summary.json          aggregate results
requirements.txt
SHA256SUMS.txt          integrity hashes for all artifact files
```

## Dataset

The artifact contains 30 positive samples: 25 Java files and five JSP files. Identifiers are neutral (`Sample001`--`Sample030`). Answer-bearing filenames, class names, routes, and explanatory comments were removed before generation. Ground truth is stored separately and was not included in generation prompts.

The dataset contains positive cases only. It supports target-defect detection rate, but it does not support precision, specificity, false-positive rate, F1, or classification accuracy.

## Method-name mapping

| Paper label | Internal identifier |
|---|---|
| Direct Prompt | `zero_shot` |
| Structured Single-Call | `single_step` |
| Sequential Three-Call | `three_stage` |

## Recorded run

- Access date: July 13, 2026
- Generation model alias: `deepseek-chat`
- Generation temperature: 0.2
- Automated-judge model alias: `deepseek-chat`
- Automated-judge temperature: 0.0
- Samples: 30
- Generation calls: 150
- Judging calls: 30
- Total calls: 180
- Total recorded usage: 206,326 tokens
- Request errors: 0
- Judgment JSON parse errors: 0

The model name is an API alias and may refer to a provider-updated model at a later date. The access date and raw outputs are therefore included for traceability.

## Reproduction

Python 3.10 or later is recommended.

```bash
python -m pip install -r requirements.txt
```

Set an API credential in the process environment. Do not add it to any artifact file.

PowerShell:

```powershell
$env:DEEPSEEK_API_KEY="YOUR_KEY"
python scripts/run_experiment.py --phase all
```

POSIX shell:

```bash
export DEEPSEEK_API_KEY="YOUR_KEY"
python scripts/run_experiment.py --phase all
```

New outputs are written to `reproduction_output/`, leaving the recorded results unchanged. The script supports resumption by skipping completed records.

## Result interpretation

The supplied quantitative results were produced by an automated judge using the same model family as the generators. They are intended as preliminary comparative evidence, not definitive human-validated accuracy. The positive-only dataset and one-run design are documented limitations.

## Anonymity

This package is designed for double-blind review. If it is hosted externally, use an anonymous artifact service and verify that the landing page, repository history, account name, commit metadata, and downloadable archive do not reveal author identity.
