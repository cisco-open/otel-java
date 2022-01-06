"""
checking that bors statuses includes all github workflows
"""
import sys

import pathlib

DEFAULT_WORKFLOW = "ci-default"
BORS_TOML_FILE = "bors.toml"
GITHUB_WORKFLOWS = ".github/workflows"
EXPECTED_WORKFLOWS_FILES = [
    "bors.yml",
]
EXTRA_STATUSES = ["ci-check-bors"]


def main():
    """
    checks what are the workflow names and compare it with these that are configured for bors
    """

    if has_unexpected_workflows_file():
        print(
            (
                f"an unexpected workflows file was found in {GITHUB_WORKFLOWS}. "
                f"expected workflows are: {EXPECTED_WORKFLOWS_FILES}"
            )
        )
        sys.exit(1)

    print(f"{BORS_TOML_FILE} status is valid")


def has_unexpected_workflows_file():
    for workflows_file in pathlib.Path(GITHUB_WORKFLOWS).iterdir():
        if workflows_file.name not in EXPECTED_WORKFLOWS_FILES:
            return True

    return False


if __name__ == "__main__":
    main()
