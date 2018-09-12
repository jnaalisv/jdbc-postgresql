-- https://www.postgresql.org/docs/current/static/datatype-datetime.html#DATATYPE-DATETIME-INPUT

-- "For timestamp with time zone, the internally stored value is always in UTC"

create table times
(
  time time,
  date date,
  timestamp timestamp,
  timestamptz timestamptz
);

insert
  into times
values
  (
      '18:51:59',
      '1999-01-08',
      '1999-01-08 18:51:01',
      '1999-01-08 18:51:45 Europe/Stockholm'
  );

-- note
select
  -- timezone will be ignored
  timestamp '1999-01-08 18:51:01 Europe/Stockholm' as ts,

  -- converted to utc
  timestamptz '1999-01-08 18:51:01 Europe/Stockholm' as tstz,

  -- if no TZ specified, uses system default TZ, so should always specify TZ
  timestamptz '1999-01-08 18:51:01' as tstz0;


-- timestamptz can be converted to different time zone
select
  time, date, timestamp,
  timestamptz as UTC,
  timestamptz at time zone 'Europe/Helsinki' as HelsinkiTime,
  timestamptz at time zone 'Europe/Stockholm' as SthlmTime
from times;


select timetz 'allballs' at time zone 'Europe/Helsinki';

select *
from pg_timezone_names

