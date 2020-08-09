import cv2
import numpy as np
from ..images import matches_template

def load(file):
    return cv2.VideoCapture(file)

def find_image(video, image_to_find, start_time=0, end_time=None):
    reverse = end_time != None and end_time < start_time
    if start_time < 0:
        start_time = duration(video) + start_time

    matches = find_image_positions(video, image_to_find, start_time, end_time)
    if reverse:
        matches.reverse()
    for m in matches:
        if m.found:
            return m
    return None

def find_image_positions(video, image_to_find, start_time=0, end_time=None):
    matches = []
    was_in_range = False

    if end_time is None:
        start = start_time
    else:
        start = min(start_time, end_time)
    if start > 0:
        video.set(cv2.CAP_PROP_POS_MSEC, start * 1000)

    while(1):
        ret, frame = video.read()
        if not ret:
            break

        time = video.get(cv2.CAP_PROP_POS_MSEC) / 1000
        #cv2.imwrite('/tmp/showcase-frames/'+str(time)+'.png', frame)
        #print("time=", time)
        if in_range(time, start_time, end_time):
            was_in_range = True
            found = matches_template(frame, image_to_find)
            #print("found=", found, "time=", time)
            matches.append(Match(time, found))
        elif was_in_range:
            break
    reset(video)
    return matches

    
def clip(video, start_time, end_time, out_file):
    target_fps = video.get(cv2.CAP_PROP_FPS)
    target_width = round(video.get(cv2.CAP_PROP_FRAME_WIDTH))
    target_height = round(video.get(cv2.CAP_PROP_FRAME_HEIGHT))
    codec = decode_fourcc(video.get(cv2.CAP_PROP_FOURCC))
#     codec = 'mp4v'

    out = cv2.VideoWriter(out_file, cv2.VideoWriter_fourcc(*codec), target_fps, (target_width, target_height))

    video.set(cv2.CAP_PROP_POS_MSEC, start_time * 1000)
    while video.get(cv2.CAP_PROP_POS_MSEC) < end_time * 1000:
        ret, frame = video.read()
        out.write(frame)
    out.release()
    

def reset(video):
    video.set(cv2.CAP_PROP_POS_FRAMES, 0)
    
def duration(video):
    return video.get(cv2.CAP_PROP_FRAME_COUNT) / video.get(cv2.CAP_PROP_FPS)
    
def in_range(time, start_time, end_time):
    if end_time is None:
        return time >= start_time
    if start_time < end_time:
        return time >= start_time and time <= end_time
    return time <= start_time and time >= end_time

def decode_fourcc(cc):
    return "".join([chr((int(cc) >> 8 * i) & 0xFF) for i in range(4)])


def extract_frame(video, frame=0):
    video.set(cv2.CAP_PROP_POS_FRAMES, frame)
    ret, frame = video.read()
    return frame

class Match:
    def __init__(self, time, found):
        self.time = time
        self.found = found

    