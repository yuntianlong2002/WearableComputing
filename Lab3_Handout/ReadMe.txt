For scheduling the alarms, you have to include both the PSMScheduler.java and EMAAlarmReceiver.java in your project.

PSMScheduler has a method called setSchedule(), which set the alarm to repeat everyday at 12.30 pm and 6.30 pm. Whenever the alarm is triggered, it sends  broadcast, which is received by EMAAlarmReceiver, which in turn starts the StressMeter activity. Both the files are pretty self explanatory.

To set the schedule, you have to call the method —
PSMScheduler.setSchedule(getActivity());
from when your activity/fragment is created.

Further in the EMAAlarmReceiver, you might have to change the MainActivity according to your usage.
