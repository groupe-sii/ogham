import cv2
import numpy as np

def load(file):
    return cv2.imread(file, 0)

def matches_template(image, template, threshold=0.5):
    img_gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    res = cv2.matchTemplate(img_gray, template, cv2.TM_CCOEFF_NORMED)
    loc = np.where(res >= threshold)
    for pt in zip(*loc[::-1]):
        #print('image found at ', pt[0], ',', pt[1])
        return True
    #print('image not found')
    return False

def save(image, file):
    cv2.imwrite(file, image)