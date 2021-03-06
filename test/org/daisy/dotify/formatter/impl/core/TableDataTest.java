package org.daisy.dotify.formatter.impl.core;

import org.daisy.dotify.api.formatter.TableCellProperties;
import org.daisy.dotify.formatter.impl.common.FormatterCoreContext;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

/**
 * TODO: Write java doc.
 */
@SuppressWarnings("javadoc")
public class TableDataTest {

    @Test
    public void testTableData() {
        FormatterCoreContext context = Mockito.mock(FormatterCoreContext.class);
        Mockito.when(context.getSpaceCharacter()).thenReturn(' ');
        TableData td = new TableData(context);
        td.beginsTableRow();
        Object a1 = td.beginsTableCell(new TableCellProperties.Builder().rowSpan(2).colSpan(2).build());
        Object a2 = td.beginsTableCell(new TableCellProperties.Builder().build());
        Object a3 = td.beginsTableCell(new TableCellProperties.Builder().rowSpan(2).build());
        td.beginsTableRow();
        Object a4 = td.beginsTableCell(new TableCellProperties.Builder().build());
        td.beginsTableRow();
        Object a5 = td.beginsTableCell(new TableCellProperties.Builder().colSpan(3).build());
        Object a6 = td.beginsTableCell(new TableCellProperties.Builder().build());
        assertEquals(a1, td.cellForGrid(0, 0));
        assertEquals(a1, td.cellForGrid(0, 1));
        assertEquals(a2, td.cellForGrid(0, 2));
        assertEquals(a3, td.cellForGrid(0, 3));
        assertEquals(a1, td.cellForGrid(1, 0));
        assertEquals(a1, td.cellForGrid(1, 1));
        assertEquals(a4, td.cellForGrid(1, 2));
        assertEquals(a3, td.cellForGrid(1, 3));
        assertEquals(a5, td.cellForGrid(2, 0));
        assertEquals(a5, td.cellForGrid(2, 1));
        assertEquals(a5, td.cellForGrid(2, 2));
        assertEquals(a6, td.cellForGrid(2, 3));
    }

}
