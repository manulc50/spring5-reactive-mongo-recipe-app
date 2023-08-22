package com.mlorenzo.spring5reactivemongorecipeapp.converters;

import com.mlorenzo.spring5reactivemongorecipeapp.commands.NotesCommand;
import com.mlorenzo.spring5reactivemongorecipeapp.domain.Notes;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class NotesCommandToNotesTest {
    public static final String ID_VALUE = "1";
    public static final String RECIPE_NOTES = "Notes";
    
    NotesCommandToNotes converter;

    @Before
    public void setUp() throws Exception {
        converter = new NotesCommandToNotes();
    }

    @Test
    public void testNullParameter() throws Exception {
        assertNull(converter.convert(null));
    }

    @Test
    public void testEmptyObject() throws Exception {
        assertNotNull(converter.convert(new NotesCommand()));
    }

    @Test
    public void convert() throws Exception {
        //given
        NotesCommand notesCommand = new NotesCommand();
        notesCommand.setId(ID_VALUE);
        notesCommand.setRecipeNotes(RECIPE_NOTES);
        //when
        Notes notes = converter.convert(notesCommand);
        //then
        assertNotNull(notes);
        assertEquals(ID_VALUE, notes.getId());
        assertEquals(RECIPE_NOTES, notes.getRecipeNotes());
    }

}