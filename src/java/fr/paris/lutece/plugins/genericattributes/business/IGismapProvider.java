package fr.paris.lutece.plugins.genericattributes.business;

import java.io.Serializable;

import fr.paris.lutece.util.ReferenceItem;

public interface IGismapProvider  extends Serializable
{
	/**
     * Gets the key. This key <b>must be unique</b>.
     * @return the key;
     */
    String getKey(  );

    /**
     * Gets the displayed name
     * @return the displayed name
     */
    String getDisplayedName(  );

    /**
     * Gets the html template
     * @return the html template
     */
    String getHtmlCode(  );
    
    /**
     * Gets the map parameter 
     * @return the map parameter 
     */
    Object getParameter(  );

    /**
     * Builds a new {@link ReferenceItem} for the map provider.<br />
     * <code>key == getKey(  )</code>,
     * <code>value == getDisplayedName(  )</code>
     * @return the item created.
     */
    ReferenceItem toRefItem(  );

}
