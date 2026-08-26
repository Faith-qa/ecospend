CREATE TABLE categories (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    impact_tag     VARCHAR(50)  NOT NULL,
    impact_weight  NUMERIC(5, 2) NOT NULL,
    CONSTRAINT uq_categories_name UNIQUE (name),
    CONSTRAINT chk_categories_impact_weight_non_negative CHECK (impact_weight >= 0)
);

CREATE TABLE transactions (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id      BIGINT NOT NULL,
    merchant     VARCHAR(200) NOT NULL,
    amount       NUMERIC(12, 2) NOT NULL,
    category_id  BIGINT NOT NULL,
    occurred_at  TIMESTAMPTZ NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_transactions_category FOREIGN KEY (category_id) REFERENCES categories (id)
);
