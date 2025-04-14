/*
 * Copyright (c) 2002-2025, City of Paris
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
package fr.paris.lutece.plugins.genericattributes.service.anonymization;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;

@ApplicationScoped
public class EntryAnonymizationTypeProducer 
{
	@Produces
	@ApplicationScoped
	@Named( "genericattributes.date0AnonymizationType" )
    public IEntryAnonymizationType produceDate0AnonymizationType( @ConfigProperty( name = "genericattributes.date0AnonymizationType.wildcard" ) String wildcard,
            @ConfigProperty( name = "genericattributes.date0AnonymizationType.helpKey" ) String helpKey,
            @ConfigProperty( name = "genericattributes.date0AnonymizationType.serviceName" ) String serviceName )
    {
        return new EntryAnonymizationType( wildcard, helpKey, serviceName );
    }

	@Produces
	@ApplicationScoped
	@Named( "genericattributes.emptyDateAnonymizationType" )
    public IEntryAnonymizationType produceEmptyDateAnonymizationType( @ConfigProperty( name = "genericattributes.emptyDateAnonymizationType.wildcard" ) String wildcard,
            @ConfigProperty( name = "genericattributes.emptyDateAnonymizationType.helpKey" ) String helpKey,
            @ConfigProperty( name = "genericattributes.emptyDateAnonymizationType.serviceName" ) String serviceName )
    {
        return new EntryAnonymizationType( wildcard, helpKey, serviceName );
    }

	@Produces
	@ApplicationScoped
	@Named( "genericattributes.defaultValueAnonymizationType" )
    public IEntryAnonymizationType produceDefaultValueAnonymizationType( @ConfigProperty( name = "genericattributes.defaultValueAnonymizationType.wildcard" ) String wildcard,
            @ConfigProperty( name = "genericattributes.defaultValueAnonymizationType.helpKey" ) String helpKey,
            @ConfigProperty( name = "genericattributes.defaultValueAnonymizationType.serviceName" ) String serviceName )
    {
        return new EntryAnonymizationType( wildcard, helpKey, serviceName );
    }

	@Produces
	@ApplicationScoped
	@Named( "genericattributes.defaultDateAnonymizationType" )
    public IEntryAnonymizationType produceDefaultDateAnonymizationType( @ConfigProperty( name = "genericattributes.defaultDateAnonymizationType.wildcard" ) String wildcard,
            @ConfigProperty( name = "genericattributes.defaultDateAnonymizationType.helpKey" ) String helpKey,
            @ConfigProperty( name = "genericattributes.defaultDateAnonymizationType.serviceName" ) String serviceName )
    {
        return new EntryAnonymizationType( wildcard, helpKey, serviceName );
    }

	@Produces
	@ApplicationScoped
	@Named( "genericattributes.defaultTelephoneAnonymizationType" )
    public IEntryAnonymizationType produceDefaultTelephoneAnonymizationType( @ConfigProperty( name = "genericattributes.defaultTelephoneAnonymizationType.wildcard" ) String wildcard,
            @ConfigProperty( name = "genericattributes.defaultTelephoneAnonymizationType.helpKey" ) String helpKey,
            @ConfigProperty( name = "genericattributes.defaultTelephoneAnonymizationType.serviceName" ) String serviceName )
    {
        return new EntryAnonymizationType( wildcard, helpKey, serviceName );
    }

	@Produces
	@ApplicationScoped
	@Named( "genericattributes.defaultGeolocAnonymizationType" )
    public IEntryAnonymizationType produceDefaultGeolocAnonymizationType( @ConfigProperty( name = "genericattributes.defaultGeolocAnonymizationType.wildcard" ) String wildcard,
            @ConfigProperty( name = "genericattributes.defaultGeolocAnonymizationType.helpKey" ) String helpKey,
            @ConfigProperty( name = "genericattributes.defaultGeolocAnonymizationType.serviceName" ) String serviceName )
    {
        return new EntryAnonymizationType( wildcard, helpKey, serviceName );
    }

	@Produces
	@ApplicationScoped
	@Named( "genericattributes.entryCodeAnonymizationType" )
    public IEntryAnonymizationType produceEntryCodeAnonymizationType( @ConfigProperty( name = "genericattributes.entryCodeAnonymizationType.wildcard" ) String wildcard,
            @ConfigProperty( name = "genericattributes.entryCodeAnonymizationType.helpKey" ) String helpKey,
            @ConfigProperty( name = "genericattributes.entryCodeAnonymizationType.serviceName" ) String serviceName )
    {
        return new EntryAnonymizationType( wildcard, helpKey, serviceName );
    }

	@Produces
	@ApplicationScoped
	@Named( "genericattributes.entryIdAnonymizationType" )
    public IEntryAnonymizationType produceEntryIdAnonymizationType( @ConfigProperty( name = "genericattributes.entryIdAnonymizationType.wildcard" ) String wildcard,
            @ConfigProperty( name = "genericattributes.entryIdAnonymizationType.helpKey" ) String helpKey,
            @ConfigProperty( name = "genericattributes.entryIdAnonymizationType.serviceName" ) String serviceName )
    {
        return new EntryAnonymizationType( wildcard, helpKey, serviceName );
    }

	@Produces
	@ApplicationScoped
	@Named( "genericattributes.randomGuidAnonymizationType" )
    public IEntryAnonymizationType produceRandomGuidAnonymizationType( @ConfigProperty( name = "genericattributes.randomGuidAnonymizationType.wildcard" ) String wildcard,
            @ConfigProperty( name = "genericattributes.randomGuidAnonymizationType.helpKey" ) String helpKey,
            @ConfigProperty( name = "genericattributes.randomGuidAnonymizationType.serviceName" ) String serviceName )
    {
        return new EntryAnonymizationType( wildcard, helpKey, serviceName );
    }

	@Produces
	@ApplicationScoped
	@Named( "genericattributes.randomNumberAnonymizationType" )
    public IEntryAnonymizationType produceRandomNumberAnonymizationType( @ConfigProperty( name = "genericattributes.randomNumberAnonymizationType.wildcard" ) String wildcard,
            @ConfigProperty( name = "genericattributes.randomNumberAnonymizationType.helpKey" ) String helpKey,
            @ConfigProperty( name = "genericattributes.randomNumberAnonymizationType.serviceName" ) String serviceName )
    {
        return new EntryAnonymizationType( wildcard, helpKey, serviceName );
    }

	@Produces
	@ApplicationScoped
	@Named( "genericattributes.responseIdAnonymizationType" )
    public IEntryAnonymizationType produceResponseIdAnonymizationType( @ConfigProperty( name = "genericattributes.responseIdAnonymizationType.wildcard" ) String wildcard,
            @ConfigProperty( name = "genericattributes.responseIdAnonymizationType.helpKey" ) String helpKey,
            @ConfigProperty( name = "genericattributes.responseIdAnonymizationType.serviceName" ) String serviceName )
    {
        return new EntryAnonymizationType( wildcard, helpKey, serviceName );
    }

	@Produces
	@ApplicationScoped
	@Named( "genericattributes.fileDeleteAnonymizationType" )
    public IEntryAnonymizationType produceFileDeleteAnonymizationType( @ConfigProperty( name = "genericattributes.fileDeleteAnonymizationType.wildcard" ) String wildcard,
            @ConfigProperty( name = "genericattributes.fileDeleteAnonymizationType.helpKey" ) String helpKey,
            @ConfigProperty( name = "genericattributes.fileDeleteAnonymizationType.serviceName" ) String serviceName )
    {
        return new EntryAnonymizationType( wildcard, helpKey, serviceName );
    }

	@Produces
	@ApplicationScoped
	@Named( "genericattributes.fileReplaceAnonymizationType" )
    public IEntryAnonymizationType produceFileReplaceAnonymizationType( @ConfigProperty( name = "genericattributes.fileReplaceAnonymizationType.wildcard" ) String wildcard,
            @ConfigProperty( name = "genericattributes.fileReplaceAnonymizationType.helpKey" ) String helpKey,
            @ConfigProperty( name = "genericattributes.fileReplaceAnonymizationType.serviceName" ) String serviceName )
    {
        return new EntryAnonymizationType( wildcard, helpKey, serviceName );
    }
}
