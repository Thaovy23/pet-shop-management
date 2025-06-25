package model.billing;

import java.math.BigDecimal;

public interface Sellable 
{
    String getDisplayName();     // For invoices, UI, etc.
    BigDecimal getPrice();       // For billing totals
}
