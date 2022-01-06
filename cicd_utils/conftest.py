"""
common cicd conftest
"""
import os.path
import pytest


@pytest.hookimpl(trylast=True)
def pytest_sessionfinish(session, exitstatus):
    """
    Ignore "no tests found" status code
    :param session: current session
    :param exitstatus: exit status
    :return: 0 if exitstatus is 5, else exitstatus
    """
    if exitstatus == 5:
        session.exitstatus = 0


def pytest_ignore_collect(path, config):  # pylint: disable=unused-argument
    """
    ignores symlinks in tests collection
    :param path: path to test
    :param config: current config
    :return: true if path is a symlink, false otherwise
    """
    return os.path.islink(path)
