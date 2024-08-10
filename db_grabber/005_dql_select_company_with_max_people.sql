SELECT c.name AS company_name, COUNT(p.id) AS employee_count
FROM person p
JOIN company c ON p.company_id = c.id
GROUP BY c.id
HAVING COUNT(p.id) = (
    SELECT MAX(employee_count)
    FROM (
        SELECT COUNT(p.id) AS employee_count
        FROM person p
        GROUP BY p.company_id
    ) AS subquery
);