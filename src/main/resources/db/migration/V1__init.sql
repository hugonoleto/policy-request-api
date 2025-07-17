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

-- Tabela de clientes bloqueados para pagamento e assinatura
CREATE TABLE customer_blocked (
    id UUID PRIMARY KEY,
    entity_type VARCHAR(13) NOT NULL CHECK (entity_type IN ('PAYMENT', 'SUBSCRIPTION', 'BOTH')),
    blocked_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- Cliente bloqueado para pagamento
INSERT INTO customer_blocked (id, entity_type, blocked_at)
VALUES ('11111111-1111-1111-1111-111111111111', 'PAYMENT', NOW());

-- Cliente bloqueado para assinatura
INSERT INTO customer_blocked (id, entity_type, blocked_at)
VALUES ('22222222-2222-2222-2222-222222222222', 'SUBSCRIPTION', NOW());

-- Cliente bloqueado para ambos (pagamento e assinatura)
INSERT INTO customer_blocked (id, entity_type, blocked_at)
VALUES ('33333333-3333-3333-3333-333333333333', 'BOTH', NOW());