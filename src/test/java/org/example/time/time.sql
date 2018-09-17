-- https://www.postgresql.org/docs/current/static/datatype-datetime.html#DATATYPE-DATETIME-INPUT

create table event (
  description varchar,
  timestamp timestamptz
);

-- "For timestamp with time zone, the internally stored value is always in UTC"

-- set the current session time zone, UTC is default
SET TIME ZONE 'UTC';
SET TIME ZONE 'Europe/Helsinki';

-- note
select
  -- timezone will be ignored
  timestamp '1999-01-08 18:51:01 Europe/Stockholm' as ts,

  -- converted to utc
  timestamptz '1999-01-08 18:51:01 Europe/Stockholm' as tstz,

  -- if no TZ specified, uses system default TZ, so should always specify TZ
  timestamptz '1999-01-08 18:51:01' as tstz0;


select timetz 'allballs' at time zone 'Europe/Helsinki';
-- 03:00:00 +03:00

select * from pg_timezone_names;
select * from pg_timezone_abbrevs;
-- "A time zone abbreviation, for example PST. Such a specification merely defines a particular offset from UTC, in contrast to full time zone names which can imply a set of daylight savings transition-date rules as well"

-- daylight savings rule applied with full time zone name, but not with time zone abbreviation
select
  timestamptz '2018-10-28 00:00' at time zone 'UTC' as UTC,
  timestamptz '2018-10-28 00:00' at time zone 'Europe/Helsinki' as utc_midnight_at_helsinki,
  timestamptz '2018-10-28 01:00' at time zone 'Europe/Helsinki' as utc_1pm_at_helsinki,
  timestamptz '2018-10-28 00:00' at time zone 'EEST' as utc_midnight_at_EEST,
  timestamptz '2018-10-28 01:00' at time zone 'EEST' as utc_1pm_at_EEST;

-- functions
select
    CURRENT_DATE,
    CURRENT_TIMESTAMP(2), -- with tz
    LOCALTIME(2),
    LOCALTIMESTAMP(2);
-- Since these functions return the start time of the current transaction,
-- their values do not change during the transaction.This is considered a feature: the
-- intent is to allow a single transaction to have a consistent notion of the “current” time,
-- so that multiple modifications within the same transaction bear the same time stamp

-- different ways of inserting timestamptz
insert
into event
values
    ('Customer meeting, Stockholm', '2018-07-08 18:25:00 Europe/Stockholm'),
    ('Shareholder dinner, Stockholm', timestamptz '2018-08-22 19:30:00 Europe/Stockholm'),
    ('Recruiting event, Helsinki', '2018-08-30 16:00:00 Europe/Helsinki')
;

select
  description
  , timestamp
  , timestamp at time zone 'Europe/Helsinki' as HelsinkiTime
  , timestamp at time zone 'Europe/Stockholm' as StockholmTime
from event;

select description, timestamp as utc, timestamp at time zone 'Europe/Helsinki' as HelsinkiTime
from event;


select
  -- converted to utc
  timestamptz '2018-05-08 18:51:01 Europe/Stockholm' as tstz,

  -- if no TZ specified, uses system default TZ, so should always specify TZ
  timestamptz '2018-08-22 18:51:01' as tstz0;


delete from event;
select
  description,
  timestamp,
  timestamp at time zone 'UTC' as utc,
  timestamp at time zone 'Europe/Helsinki' as HelsinkiTime,
  timestamp at time zone 'Europe/Stockholm' as StockholmTime
from event;