SELECT p.name AS person_name, c.name AS company_name
FROM person p
JOIN company c ON p.company_id = c.id
WHERE p.company_id != 5;
