package br.com.justdev.picnotes;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by JustGian on 11/12/2016.
 */
public class NavItemTest {

    NavItem item;

    final String TITLE = "Item do Menu";
    final String SUBTITLE = "Subtitulo";
    final int ICON = 12;

    @Before
    public void create() throws Exception {
        item = new NavItem(TITLE, SUBTITLE, ICON);
    }

    @Test
    public void getTitle() throws Exception {
        assertEquals(item.getTitle(), TITLE);
    }

    @Test
    public void getSubtitle() throws Exception {
        assertEquals(item.getSubtitle(), SUBTITLE);
    }

    @Test
    public void getIcon() throws Exception {
        assertEquals(item.getIcon(), ICON);
    }

}