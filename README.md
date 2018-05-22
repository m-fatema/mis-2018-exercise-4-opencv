# mis-2018-exercise-4-opencv

We have use haarcascade classifier to detect frontal_face & nose.

The nose detector returns a set of points in the form of rectangle around the nose of the person looking in the camera.
From these set of points we compute the point near the nose tip, which is the center of the circle.

We have calculated the radius of the circle depending upon the widht of the nose rectangle returned by the nose detector. 

By testing with differnt devices & different face size we have multiplied the nose width by 0.25 and set this as radius of the circle.

We have tested with front camera:
With front camera, this app only works in landscape mode and only in one view of landscape orientation, i.e, if we rotate the mobile by 180 degree in landscape orientation, the nose is not detected.
