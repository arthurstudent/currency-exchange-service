CREATE TABLE IF NOT EXISTS currencies (
                                              id SERIAL PRIMARY KEY,
                                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                              currency VARCHAR(10) UNIQUE NOT NULL
    );

CREATE TABLE IF NOT EXISTS exchange_rates (
                                              id SERIAL PRIMARY KEY,
                                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                              amount NUMERIC NOT NULL,
                                              target_currency VARCHAR(10) NOT NULL,
    currency_id INT NOT NULL,
    FOREIGN KEY (currency_id) REFERENCES currencies (id) ON DELETE CASCADE
    );