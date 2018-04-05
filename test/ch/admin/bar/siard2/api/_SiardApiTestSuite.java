package ch.admin.bar.siard2.api;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	ArchiveTester.class,
	MetaAttributeTester.class,
  MetaColumnTester.class,
  MetaDataTester.class,
  MetaFieldTester.class,
  MetaParameterTester.class,
  MetaRoutineTester.class,
  MetaSchemaTester.class,
  MetaSearchTester.class,
  MetaTableTester.class,
  MetaTypeTester.class,
  MetaViewTester.class,
  RecordExtractTester.class,
  RecordTester.class,
  SchemaTester.class,
  TableTester.class
})
public class _SiardApiTestSuite {

}
