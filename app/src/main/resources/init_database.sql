-- account_transactions
ALTER TABLE account_transactions RENAME COLUMN expenditure TO expenditure_old;
ALTER TABLE account_transactions DROP CONSTRAINT account_transactions_expenditure_check ;
ALTER TABLE account_transactions ADD COLUMN expenditure smallint check (expenditure >= 0 AND expenditure <= 20);

UPDATE account_transactions SET expenditure=0 WHERE expenditure_old='FOOD';
UPDATE account_transactions SET expenditure=1 WHERE expenditure_old='GOODS';
UPDATE account_transactions SET expenditure=2 WHERE expenditure_old='FUN';
UPDATE account_transactions SET expenditure=3 WHERE expenditure_old='HEALTH';
UPDATE account_transactions SET expenditure=4 WHERE expenditure_old='TRANSPORT';
UPDATE account_transactions SET expenditure=5 WHERE expenditure_old='TELECOMMUNICATIONS';
UPDATE account_transactions SET expenditure=6 WHERE expenditure_old='HOUSE';
UPDATE account_transactions SET expenditure=7 WHERE expenditure_old='TRAVEL';
UPDATE account_transactions SET expenditure=8 WHERE expenditure_old='TAXES';
UPDATE account_transactions SET expenditure=10 WHERE expenditure_old='OTHER';

ALTER TABLE account_transactions ALTER COLUMN expenditure SET NOT NULL;
ALTER TABLE account_transactions DROP COLUMN expenditure_old;

-- account_transactions
ALTER TABLE merchant_expenditures RENAME COLUMN expenditure TO expenditure_old;
ALTER TABLE merchant_expenditures DROP CONSTRAINT merchant_expenditures_expenditure_check ;
ALTER TABLE merchant_expenditures ADD COLUMN expenditure smallint check (expenditure >= 0 AND expenditure <= 20);

UPDATE merchant_expenditures SET expenditure=0 WHERE expenditure_old='FOOD';
UPDATE merchant_expenditures SET expenditure=1 WHERE expenditure_old='GOODS';
UPDATE merchant_expenditures SET expenditure=2 WHERE expenditure_old='FUN';
UPDATE merchant_expenditures SET expenditure=3 WHERE expenditure_old='HEALTH';
UPDATE merchant_expenditures SET expenditure=4 WHERE expenditure_old='TRANSPORT';
UPDATE merchant_expenditures SET expenditure=5 WHERE expenditure_old='TELECOMMUNICATIONS';
UPDATE merchant_expenditures SET expenditure=6 WHERE expenditure_old='HOUSE';
UPDATE merchant_expenditures SET expenditure=7 WHERE expenditure_old='TRAVEL';
UPDATE merchant_expenditures SET expenditure=8 WHERE expenditure_old='TAXES';
UPDATE merchant_expenditures SET expenditure=10 WHERE expenditure_old='OTHER';

ALTER TABLE merchant_expenditures ALTER COLUMN expenditure SET NOT NULL;
ALTER TABLE merchant_expenditures DROP COLUMN expenditure_old;