package ch.admin.bar.siard2.api;

import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.enterag.utils.EU;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@Ignore("file is not available in repository")
public class BugTester
{
  private static final File _fileBUG49 = new File("..\\Bugs\\479\\dvd_rental.siard");

  @Test
  public void test()
  {
    try
    {
      Archive archive = ArchiveImpl.newInstance();
      archive.open(_fileBUG49);
      Schema schema = archive.getSchema("public");
      Table table = schema.getTable("film");
      RecordDispenser rd = table.openRecords();
      Record record = rd.get();
      Cell cell = record.getCell(11);
      MetaColumn mc = cell.getMetaColumn();
      System.out.println("Cardinality: "+String.valueOf(mc.getCardinality()));
      System.out.println("Fields: "+String.valueOf(mc.getMetaFields()));
      assertEquals("",4,cell.getElements());
      List<Value> listValues = record.getValues(false,false);
      assertEquals("",16,listValues.size());
      rd.close();
      archive.close();
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  }

}
