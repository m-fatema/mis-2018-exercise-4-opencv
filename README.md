# mis-2018-exercise-4-opencv

We have use haarcascade classifier to detect frontal_face. 

The center of circle for showinf red nose is calculated by the points of the face detected from frontal_face_detection: 

x = (2x + width)  * 0.49, 
y = (2y + height)  * 0.53.  
To place the red dot on nose tip we have multiplied by 0.53 & 0.49. We have used this particular value by testing the app with different face sizes & devices of different screen size. 
Using the same method we have divided the face width by 10 and set this as radius of the circle. 

We have tested with front camera: 
With front camera, this app only works in landscape mode.
