-- Tabela principal da solicitação de apólice
CREATE TABLE policy_request (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    product_id UUID NOT NULL,
    category VARCHAR(50) NOT NULL,
    sales_channel VARCHAR(30) NOT NULL,
    payment_method VARCHAR(30) NOT NULL,
    status VARCHAR(24) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    finished_at TIMESTAMP WITH TIME ZONE,
    total_monthly_premium_amount NUMERIC(15,2) NOT NULL,
    insured_amount NUMERIC(15,2) NOT NULL
);

-- Coberturas da solicitação (nome e valor)
CREATE TABLE policy_request_coverage (
    policy_request_id UUID REFERENCES policy_request(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    amount NUMERIC(15,2) NOT NULL,
    PRIMARY KEY (policy_request_id, name)
);

-- Assistências da solicitação (lista de strings)
CREATE TABLE policy_request_assistance (
    policy_request_id UUID REFERENCES policy_request(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    PRIMARY KEY (policy_request_id, name)
);

-- Histórico de status da solicitação
CREATE TABLE policy_request_history (
    id SERIAL PRIMARY KEY,
    policy_request_id UUID REFERENCES policy_request(id) ON DELETE CASCADE,
    status VARCHAR(24) NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL
);