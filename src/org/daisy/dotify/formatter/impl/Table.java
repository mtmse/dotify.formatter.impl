package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.daisy.dotify.api.formatter.FormatterCore;
import org.daisy.dotify.api.formatter.TableCellProperties;
import org.daisy.dotify.common.text.StringTools;

class Table extends Block {
	private int headerRows;
	private final Stack<TableRow> rows;

	Table(RowDataProperties props) {
		super(null, props);
		headerRows = 0;
		rows = new Stack<>();
	}

	public void beginsTableBody() {
		headerRows = rows.size();
	}

	public void beginsTableRow() {
		if (!rows.empty()) {
			rows.peek().endsTableCell();
		}
		TableRow ret = new TableRow();
		rows.add(ret);
	}

	public FormatterCore beginsTableCell(TableCellProperties props) {
		return rows.peek().beginsTableCell(props);
	}

	/**
	 * 
	 * @return
	 * @throws IllegalStateException
	 */
	private TableCell getCurrentCell() {
		return rows.peek().getCurrentCell();
	}

	@Override
	public void addSegment(TextSegment s) {
		((FormatterCoreImpl)getCurrentCell()).getCurrentBlock().addSegment(s);
	}
	
	@Override
	public void addSegment(Segment s) {
		((FormatterCoreImpl)getCurrentCell()).getCurrentBlock().addSegment(s);
	}

	@Override
	protected AbstractBlockContentManager newBlockContentManager(BlockContext context) {
		int columnWidth = context.getFlowWidth() / countColumns();
		DefaultContext dc = DefaultContext.from(context.getContext()).metaVolume(metaVolume).metaPage(metaPage).build();
		List<RowImpl> result = new ArrayList<RowImpl>();
		MarginProperties leftMargin = rdp.getLeftMargin().buildMargin(context.getFcontext().getSpaceCharacter());
		MarginProperties rightMargin = rdp.getRightMargin().buildMargin(context.getFcontext().getSpaceCharacter());
		for (TableRow row : rows) {
			List<List<RowImpl>> cellData = new ArrayList<>(); 
			for (TableCell cell : row) {
				List<Block> blocks = cell.getBlocks(context.getFcontext(), dc, context.getRefs());
				List<RowImpl> rowData = new ArrayList<>();
				for (Block block : blocks) {
					AbstractBlockContentManager bcm = block.getBlockContentManager(
							new BlockContext(columnWidth*cell.getColSpan(), context.getRefs(), dc, context.getFcontext())
							);
					//FIXME: get additional data from bcm
					rowData.addAll(bcm.getCollapsiblePreContentRows());
					rowData.addAll(bcm.getInnerPreContentRows());
					for (RowImpl r2 : bcm) {
						rowData.add(r2);
					}
					rowData.addAll(bcm.getPostContentRows());
					rowData.addAll(bcm.getSkippablePostContentRows());
				}
				cellData.add(rowData);
			}
			// render into rows
			for (int i=0; ; i++) {
				boolean empty = true;
				StringBuilder tableRow = new StringBuilder();
				for (int j=0; j<cellData.size(); j++) {
					List<RowImpl> cr = cellData.get(j);
					String data = "";
					if (i<cr.size()) {
						empty = false;
						//FIXME: get additional properties, such as left margin etc.
						data = cr.get(i).getChars();
					}
					//fill
					int length = columnWidth - data.length();
					//FIXME: left alignment is assumed?
					tableRow.append(data);
					// Only append after intermediary columns 
					if (j<cellData.size()-1) {
						tableRow.append(StringTools.fill(context.getFcontext().getSpaceCharacter(), length));
					}
				}
				if (empty) {
					break;
				} else {
					result.add(new RowImpl(tableRow.toString(), leftMargin, rightMargin));
				}
			}
		}
		return new TableBlockContentManager(context.getFlowWidth(), result, rdp, context.getFcontext());
	}
	
	private int countColumns() {
		int cc = 0;
		// calculate the number of columns based on the first row
		// if subsequent rows differ, report it as an error
		for (TableCell c : rows.get(0)) {
			cc += Math.max(c.getColSpan(), 1);
		}
		return cc;
	}

}