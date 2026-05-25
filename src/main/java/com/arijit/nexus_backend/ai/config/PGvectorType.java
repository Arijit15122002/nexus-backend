package com.arijit.nexus_backend.ai.config;

import com.pgvector.PGvector;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import java.io.Serializable;
import java.sql.*;

public class PGvectorType implements UserType<PGvector> {

    @Override
    public int getSqlType() { return Types.OTHER; }

    @Override
    public Class<PGvector> returnedClass() { return PGvector.class; }

    @Override
    public boolean equals(PGvector x, PGvector y) {
        return x == y || (x != null && x.equals(y));
    }

    @Override
    public int hashCode(PGvector x) { return x == null ? 0 : x.hashCode(); }

    @Override
    public PGvector nullSafeGet(ResultSet rs, int position,
                                SharedSessionContractImplementor session, Object owner) throws SQLException {
        String value = rs.getString(position);
        return value == null ? null : new PGvector(value);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, PGvector value,
                            int index, SharedSessionContractImplementor session) throws SQLException {
        if (value == null) st.setNull(index, Types.OTHER);
        else st.setObject(index, value, Types.OTHER);
    }

    @Override
    public PGvector deepCopy(PGvector value) { return value; }

    @Override
    public boolean isMutable() { return false; }

    @Override
    public Serializable disassemble(PGvector value) {
        return value == null ? null : value.toString();
    }

    @Override
    public PGvector assemble(Serializable cached, Object owner) {
        if (cached == null) return null;
        try {
            return new PGvector((String) cached);
        } catch (Exception e) {
            throw new RuntimeException("Failed to assemble PGvector", e);
        }
    }
}