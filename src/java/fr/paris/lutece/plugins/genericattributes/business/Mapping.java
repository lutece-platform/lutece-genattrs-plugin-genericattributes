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

import java.io.Serializable;

/**
 * class Field
 */
public class Mapping implements Serializable
{
	private static final long serialVersionUID = -4198261124477108458L;
	
	private int _nIdMapping;
    private int _nIdStep;
    private int _nIdQuestion;
    private String _strQuestionTitle;
    private int _nIdFieldOcr;
    private String _strFieldOcrTitle;
    
	public int getIdMapping() {
		return _nIdMapping;
	}
	
	public void setIdMapping(int idMapping) {
		this._nIdMapping = idMapping;
	}
	
	public int getIdStep() {
		return _nIdStep;
	}
	
	public void setIdStep(int idStep) {
		this._nIdStep = idStep;
	}
	
	public int getIdQuestion() {
		return _nIdQuestion;
	}
	
	public void setIdQuestion(int idQuestion) {
		this._nIdQuestion = idQuestion;
	}
	public int getIdFieldOcr() {
		return _nIdFieldOcr;
	}
	public void setIdFieldOcr(int idFieldOcr) {
		this._nIdFieldOcr = idFieldOcr;
	}

	public String getQuestionTitle() {
		return _strQuestionTitle;
	}

	public void setQuestionTitle(String _questionTitle) {
		this._strQuestionTitle = _questionTitle;
	}

	public String getFieldOcrTitle() {
		return _strFieldOcrTitle;
	}

	public void setFieldOcrTitle(String fieldOcrTitle) {
		this._strFieldOcrTitle = fieldOcrTitle;
	}
	
	
}
