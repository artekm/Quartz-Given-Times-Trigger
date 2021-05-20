# Quartz-Given-Times-Trigger
## New trigger for Quartz

This trigger is an implementation of Trigger interface.
Allows to specify time of execution of a job as hours of day
Also days of week can be specified.

With provided extension of SimplePropertiesTriggerPersistenceDelegateSupport the triger can be used with database
jobStore, trigger data is kept in QRTZ_SIMPROP_TRIGGERS table.

You need to define in builder:
- a list of LocalTime as fireTimes
- a list of DayOfWeek as fireDays

Please look at QuartzApplication class for usage example.
