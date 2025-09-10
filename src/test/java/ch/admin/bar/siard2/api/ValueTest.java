package ch.admin.bar.siard2.api;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.sql.Types;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;


@ExtendWith(MockitoExtension.class)
public class ValueTest {

    @Spy
    Value value;

    @Mock
    MetaValue metaValue;

    @BeforeEach
    void setUp() {
        doReturn(metaValue).when(value).getMetaValue();
    }

    @Test
    @DisplayName("Converts a smallint value")
    public void shouldConvertValue_SmallInt() throws IOException {
        doReturn(1).when(value).getInt();
        doReturn(Types.SMALLINT).when(metaValue).getPreType();

        String result = value.convert();

        Assertions.assertEquals("1", result);
    }
}