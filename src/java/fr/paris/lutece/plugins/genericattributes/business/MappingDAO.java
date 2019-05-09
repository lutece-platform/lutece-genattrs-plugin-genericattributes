/*
 * Copyright (c) 2002-2014, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.genericattributes.business;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Mapping objects
 */
public final class MappingDAO implements IMappingDAO
{
	
	private static final String SQL_QUERY_FIND_BY_ID = "SELECT id_mapping,id_step,id_question,id_field_ocr, question_title, field_ocr_title"
            + " FROM genatt_mapping_file_reading  WHERE id_step = ? ORDER BY id_mapping";
	private static final String SQL_QUERY_FIND_QUESTIONS_MAPPED_BY_ENTRY_ID = "select mapping.id_question from forms_question question  " + 
			"join genatt_mapping_file_reading mapping on mapping.id_step = question.id_step " + 
			"where question.id_entry=?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO genatt_mapping_file_reading(id_mapping, id_step, id_question, id_field_ocr, question_title, field_ocr_title)"
            + " VALUES(?,?,?,?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM genatt_mapping_file_reading WHERE id_mapping = ? ";
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_mapping ) FROM genatt_mapping_file_reading";

    /**
     * {@inheritDoc}
     */
	@Override
	public int insert(Mapping mapping, Plugin plugin) {
		mapping.setIdMapping(newPrimaryKey( plugin ));
		
		DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        
		daoUtil.setInt( 1, mapping.getIdMapping());
        daoUtil.setInt( 2, mapping.getIdStep());
        daoUtil.setInt( 3, mapping.getIdQuestion());
        daoUtil.setInt( 4, mapping.getIdFieldOcr());
        daoUtil.setString( 5, mapping.getQuestionTitle());
        daoUtil.setString( 6, mapping.getFieldOcrTitle());
        
        daoUtil.executeUpdate( );
        daoUtil.close();
        
        return mapping.getIdMapping();
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public void delete(int nIdMapping, Plugin plugin) {
		DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdMapping );
        daoUtil.executeUpdate( );
        daoUtil.close( );
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public List<Mapping> loadByStepId(int nIdStep, Plugin plugin) {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_ID, plugin );
        daoUtil.setInt( 1, nIdStep );
        daoUtil.executeQuery( );

        List<Mapping> mappingList = new ArrayList<>();

        while ( daoUtil.next( ) )
        {
        	Mapping mapping = new Mapping( );
        	mapping.setIdMapping( daoUtil.getInt( 1 ) );
        	mapping.setIdStep( daoUtil.getInt( 2 ) );
        	mapping.setIdQuestion( daoUtil.getInt( 3 ) );
        	mapping.setIdFieldOcr( daoUtil.getInt( 4 ) );
        	mapping.setQuestionTitle( daoUtil.getString( 5 ) );
        	mapping.setFieldOcrTitle( daoUtil.getString( 6 ) );
            
            mappingList.add( mapping );
        }

        daoUtil.close( );

        return mappingList;
	}
	
	public List<Integer> loadQuestionsMappedByEntryId( int nIdEntry, Plugin plugin ) {
		DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_QUESTIONS_MAPPED_BY_ENTRY_ID, plugin );
        daoUtil.setInt( 1, nIdEntry );
        daoUtil.executeQuery( );

        List<Integer> questionMappedList = new ArrayList<>();

        while ( daoUtil.next( ) )
        {
        	questionMappedList.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.close( );

        return questionMappedList;
	}
    
	/**
     * Generates a new primary key
     *
     * @param plugin
     *            the plugin
     * @return The new primary key
     */
    public int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery( );

        int nKey;

        if ( !daoUtil.next( ) )
        {
            // if the table is empty
            nKey = 1;
        } else {
        	nKey = daoUtil.getInt( 1 ) + 1;
        }

        daoUtil.close( );

        return nKey;
    }
}
