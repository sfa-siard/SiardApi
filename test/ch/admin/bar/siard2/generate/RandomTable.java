package ch.admin.bar.siard2.generate;

import java.io.IOException;

import ch.admin.bar.siard2.api.*;

public class RandomTable
{
  private Table _table = null;

  RandomTable(Table table)
  {
    _table = table;
  } /* constructor */

  public int createTable()
  {
    int iReturn = RandomArchive.iRETURN_OK;
    try
    {
      RecordRetainer rr = _table.createRecords();
      for (long lRecord = 0; (iReturn == RandomArchive.iRETURN_OK) && (lRecord < _table.getMetaTable().getRows()); lRecord++)
      {
        Record record = rr.create();
        RandomRecord rrec = new RandomRecord(record);
        iReturn = rrec.createRecord();
        rr.put(record);
        if ((lRecord % 1000) == 999)
        {
          System.out.print('.');
          if ((lRecord % 80000) == 79999)
            System.out.println();
        }
      }
      rr.close();
      if ((_table.getMetaTable().getRows() & 80000) != 0)
        System.out.println();
    }
    catch (IOException ie) { System.err.println(RandomArchive.getExceptionMessage(ie)); }
    return iReturn;
  }
  
}
