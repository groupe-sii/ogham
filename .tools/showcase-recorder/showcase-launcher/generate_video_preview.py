import argparse
from utils import images
from utils import videos
import cv2
import numpy as np

def add_player_controls(preview, controls):
    preview_height, preview_width = preview.shape[:2]
    controls_height, controls_width = controls.shape[:2]
    scale = preview_width / controls_width
    target_height = int(controls_height * scale)
    controls_resized = cv2.resize(controls, (preview_width, target_height))
    preview = cv2.cvtColor(preview, cv2.COLOR_BGR2GRAY)
    preview = preview[0:preview_height-50, 0:preview_width]
    return cv2.vconcat([preview, controls_resized])
    
    
if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Load recorded video and extract a frame as prevew')
    parser.add_argument('video')
    parser.add_argument('preview')
    parser.add_argument('--frame', default=10)
    args = parser.parse_args()

    # only keep first frame of the video
    video = videos.load(args.video)
    preview = videos.extract_frame(video, args.frame)
    preview = add_player_controls(preview, images.load('player-controls.png'))
    images.save(preview, args.preview)
    video.release()
