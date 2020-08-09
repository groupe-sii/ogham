import argparse
from utils import images
from utils import videos

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Load recorded video and keep only the showcase part')
    parser.add_argument('video')
    parser.add_argument('clipped')
    #parser.add_argument('--selenoid-background-image', default='selenoid.png')
    args = parser.parse_args()

    # only keep showcase part of the video
    video = videos.load(args.video)
    start = videos.find_image(video, images.load('title.png'), start_time=0, end_time=30)
    end = videos.find_image(video, images.load('selenoid.png'), start_time=-30)
    clipped_start = 0 if start is None else start.time
    clipped_end = videos.duration(video) if end is None else end.time
    videos.clip(video, clipped_start, clipped_end-1, args.clipped)
    video.release()
