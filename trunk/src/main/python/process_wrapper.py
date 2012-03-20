#!/usr/bin/python

import subprocess
import time

class ProcessWrapper(object):
    def __init__(self, arg_list):
        self.__arg_list = arg_list

    def start(self):
        self.__process = subprocess.Popen(self.__arg_list);

    def stop(self, timeout_seconds = 1):
        self.__process.terminate()
        timeout_at = time.time() + timeout_seconds
        self.wait_for_finish_with_timeout(timeout_at)
        if not self.is_finished():
            self.kill()
        self.wait_for_finish_with_timeout(timeout_at)
        if not self.is_finished():
            raise RuntimeError("failed to stop process within timeout")

    def kill(self):
        self.__process.kill()

    def is_finished(self):
        return self.__process.poll() is not None

    def poll(self):
        return self.__process.poll()

    def wait_for_finish_with_timeout(self, timeout_at):
        while time.time() < timeout_at and not self.is_finished():
            time.sleep(0.1)


