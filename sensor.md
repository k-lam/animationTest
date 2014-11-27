##coordinate 

###world coordinate 

X is defined as the vector product Y.Z (It is tangential to the ground at the device's current location and roughly points East).

Y is tangential to the ground at the device's current location and points towards the magnetic North Pole.

Z points towards the sky and is perpendicular to the ground.

![](http://developer.android.com/images/axis_globe.png)

###[Sensor Coordinate System](http://developer.android.com/guide/topics/sensors/sensors_overview.html#sensors-coords)



###device /sensor / screen coordinate

In general, the sensor framework uses a standard 3-axis coordinate system to express data values. For most sensors, the coordinate system is defined **relative to the device's screen** when the device is held in its default orientation (see figure 1). When a device is held in its default orientation, the X axis is horizontal and points to the right, the Y axis is vertical and points up, and the Z axis points toward the outside of the screen face. In this system, coordinates behind the screen have negative Z values. This coordinate system is used by the following sensors:

Figure 1. Coordinate system (relative to a device) that's used by the Sensor API.

* Acceleration sensor
* Gravity sensor
* Gyroscope
* Linear acceleration sensor
* Geomagnetic field senso


The most important point to understand about this coordinate system is that the axes are not swapped when the device's screen orientation changes—that is, the sensor's coordinate system never changes as the device moves.

![](http://developer.android.com/images/axis_device.png)


![](http://4.bp.blogspot.com/-U3qaOUbRnls/Uzc9zPcPN2I/AAAAAAAAAU4/nzMr6luhpos/s400/figureA.png)

![](http://1.bp.blogspot.com/-VzDbsjPgcdc/Uzc9RHJEAHI/AAAAAAAAAJc/V2IBmzhXNXY/s1600/FigureB.png)


###Rotation

[Rotation matrix](http://en.wikipedia.org/wiki/Rotation_matrix)

[ Yaw (Azimuth), pitch, and roll](http://en.wikipedia.org/wiki/Aircraft_principal_axes)

![](http://upload.wikimedia.org/wikipedia/commons/thumb/c/c1/Yaw_Axis_Corrected.svg/319px-Yaw_Axis_Corrected.svg.png)

![](http://www.mathworks.com/matlabcentral/fileexchange/40876-android-sensor-support-from-matlab--r2013a--r2013b-/content/sensorgroup/Examples/html/Figure1.jpg)


####[SensorManager.getOrientation(float[] R, float[] values)](http://developer.android.com/reference/android/hardware/SensorManager.html)

* values[0]: azimuth, rotation around the Z axis.
* values[1]: pitch, rotation around the X axis.
* values[2]: roll, rotation around the Y axis.

As you can see, the positive X-axis extends out of the right side of the phone, positive Y-axis extends out of the top side, and the positive Z-axis extends out of the front face of the phone. This is independent of the orientation of the phone.

**Definition of Azimuth, Pitch, and Roll**

Azimuth is angle between the positive Y-axis and magnetic north and its range is between 0 and 360 degrees.

Positive Pitch is defined when the phone starts by laying flat on a table and the positive Z-axis begins to tilt towards the positive Y-axis.

Positive Roll is defined when the phone starts by laying flat on a table and the positive Z-axis begins to tilt towards the positive X-axis.





