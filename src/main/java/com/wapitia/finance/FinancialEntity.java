
package com.wapitia.finance;

public class FinancialEntity {

    private final String ident;
    private final String name;

    public FinancialEntity(String ident, String name) {
        this.ident = ident;
        this.name = name;
    }

    public static FinancialEntity makeNamedEntity(final String init,
        final String name)
    {

        return new FinancialEntity(init, name);
    }

    public String getIdent() {

        return ident;
    }

    public String getName() {

        return name;
    }

    @Override
    public String toString() {

        return ident;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this)
            return true;
        else if (o == null || !(o instanceof FinancialEntity))
            return false;
        FinancialEntity oth = (FinancialEntity) o;
        return this.getIdent().equals(oth.getIdent())
            && this.getName().equals(oth.getName());
    }

    @Override
    public int hashCode() {

        return ident.hashCode() << 3 + name.hashCode();
    }
}
