import argparse
from moviepy.editor import VideoFileClip
from _ctypes import resize


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Convert video to animated gif')
    parser.add_argument('video')
    parser.add_argument('gif')
    args = parser.parse_args()

    (VideoFileClip(args.video)
        .crop(x1=270, x2=1645, y1=80)
        .resize(0.5)
        .write_gif(args.gif, fps=5))
