# mis-2018-exercise-4-opencv

We have use haarcascade classifier to detect frontal_face.

By testing with differnt devices & different face size we have divided the face width by 10 and set this as radius of the circle.

The center of circle fpr nose is calculated by the points of the face detected from frontal_face_detection:
x = (2x + width)  * 0.49
y = x = (2y + height)  * 0.53

We have tested with front camera:
With front camera, this app only works in landscape mode and only in one view of landscape orientation, i.e, if we rotate the mobile by 180 degree in landscape orientation, the nose is not detected.
