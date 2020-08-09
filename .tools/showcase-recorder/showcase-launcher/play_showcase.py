import argparse
from selenium import webdriver
from selenium.webdriver.support.wait import WebDriverWait
from selenium.webdriver.support import expected_conditions as ec
from selenium.webdriver.common.by import By
import time
import socket
import os
import os.path
from pathlib import Path

def clean(file):
    try:
        os.remove(file)
    except OSError:
        pass


def chrome(url, video_name, wait_timeout):
    capabilities = {
        "browserName": "chrome",
        "version": "84.0",
        "enableVNC": True,
        "enableVideo": True,
        "videoName": video_name
    }
    chrome = webdriver.Remote(command_executor='http://selenoid:4444/wd/hub',
                              desired_capabilities=capabilities)
    chrome.set_window_rect(-4, -125, 1928, 1208)
    # this is needed since Chrome has not docs entry host in loaded /etc/hosts
    fixed_url = url.replace('docs', socket.gethostbyname('docs'))
    print('Opening Chrome on '+fixed_url)
    chrome.get(fixed_url)
    #chrome.find_element_by_css_selector('body').send_keys('f')
    WebDriverWait(chrome, wait_timeout).until(ec.visibility_of_element_located((By.CSS_SELECTOR, '.the-end')))
    chrome.quit()

def wait_until_video_is_exported(file, timeout):
    time_counter = 0
    while not os.path.exists(file):
        time.sleep(1)
        time_counter += 1
        if time_counter > timeout:break
    
if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Run chrome browser on provided url.')
    parser.add_argument('url')
    parser.add_argument('--slide-end-timeout', default=300)
    parser.add_argument('--recorded-video', default='/tmp/showcase.mp4')
    parser.add_argument('--recorded-video-timeout', default=120)
    args = parser.parse_args()

    clean(args.recorded_video)
    chrome(args.url, Path(args.recorded_video).name, args.slide_end_timeout)
    # wait for video to be ready and renamed
    wait_until_video_is_exported(args.recorded_video, args.recorded_video_timeout)
    