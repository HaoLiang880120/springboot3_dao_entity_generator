package individual.hl_so2.gen_db;

public record TableColumnMetadata(
        String columnName,
        Boolean nullable,
        String dataType,
        String comment,
        int position
) {}
