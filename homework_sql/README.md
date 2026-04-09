Задание №74
```
SELECT
    id,
    CASE
        WHEN has_internet = TRUE THEN 'YES'
        ELSE 'NO'
    END AS has_internet
FROM
    Rooms;
```

Задание №56
```
DELETE FROM Trip where town_from='Moscow'
```

Задание №114
```
SELECT
    p.name
FROM
    Pilots p
JOIN
    Flights f ON p.pilot_id = f.second_pilot_id
WHERE
    f.destination = 'New York'
    AND TO_CHAR(f.flight_date, 'YYYY-MM') = '2023-08';
```

Задание №19
```
SELECT DISTINCT fm.status
FROM FamilyMembers fm
	JOIN Payments p ON fm.member_id = p.family_member
	JOIN Goods g ON p.good = g.good_id
WHERE g.good_name = 'potato';
```

Задание №21
```
SELECT g.good_name
FROM Goods g
	JOIN Payments p ON g.good_id = p.good
GROUP BY g.good_id,
	g.good_name
HAVING COUNT(*) > 1;
```

Задание №32
```
SELECT FLOOR(
		AVG(
			EXTRACT(
				YEAR
				FROM age(NOW(), birthday)
			)
		)
	) AS age
FROM FamilyMembers;
```