import sys
from pathlib import Path
import pkg_resources

# _REQUIREMENTS_PATH = Path(__file__).parent.with_name("requirements.txt")
_REQUIREMENTS_PATH = Path(__file__).with_name("requirements.txt")


def test_requirements():
    """Test that each required package is available."""
    requirements = pkg_resources.parse_requirements(_REQUIREMENTS_PATH.open())
    has_conflict = False
    for requirement in requirements:
        requirement = str(requirement)
        try:
            pkg_resources.require(requirement)
        except pkg_resources.VersionConflict as e:
            print(e, file=sys.stderr)
            has_conflict = True

    if has_conflict:
        print("Some dependencies do not exist or do not match the version, please execute the install.sh script",
              file=sys.stderr)
        sys.exit(1)


test_requirements()
