-- Product Service Initial Schema

-- Airlines table
CREATE TABLE airlines (
    airline_code VARCHAR(3) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    logo_url VARCHAR(255),
    country VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    alliance_code VARCHAR(10),
    alliance_name VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Airports table
CREATE TABLE airports (
    airport_code VARCHAR(3) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    timezone VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Aircraft types table
CREATE TABLE aircraft_types (
    aircraft_type_code VARCHAR(10) PRIMARY KEY,
    manufacturer VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Aircraft table
CREATE TABLE aircraft (
    aircraft_id VARCHAR(36) PRIMARY KEY,
    registration_number VARCHAR(20) NOT NULL UNIQUE,
    airline_code VARCHAR(3) NOT NULL REFERENCES airlines(airline_code),
    aircraft_type_code VARCHAR(10) NOT NULL REFERENCES aircraft_types(aircraft_type_code),
    seat_capacity INT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Product audit table
CREATE TABLE product_audit (
    audit_id BIGSERIAL PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    entity_id VARCHAR(36) NOT NULL,
    action VARCHAR(10) NOT NULL,
    user_id VARCHAR(36),
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    old_value JSONB,
    new_value JSONB
);

-- Create indexes
CREATE INDEX idx_product_audit_entity ON product_audit(entity_type, entity_id);
CREATE INDEX idx_product_audit_timestamp ON product_audit(timestamp);
