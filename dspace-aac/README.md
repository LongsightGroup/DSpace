# Installation instructions

Installation of a Editable Authority Control requires the following steps to be completed:

1. [Copying](#copy-dspace-aac-module) of the `dspace-aac` module into DSpace.
2. [Copying](#mirage2-customizations) of Mirage2 customizations into DSpace.
3. [Alteration](#build-configuration) of DSpace Maven poms with the appropriate settings.
4. [Alteration](#eac-configuration) of EAC configurations in the DSpace config directory.
5. [Alteration](#authority-source-configuration) of Authority Source configurations in `orcid-authorty-sources.xml`.
6. [Alteration](#solr-configuration) of the authority Solr schema to support additional fields.
7. [Alteration](#assembly-configuration) of the assembly process to support parameterization in spring files.


## Copy `dspace-aac` Module

Simply copy the contents of the `dspace-aac` module into your DSpace.

## Mirage2 Customizations

Changes are necessary to the following files. It is necessary to copy them from the original source into the
corresponding directory under `dspace/modules/xmlui-mirage2/src/main/webapps/themes/Mirage2/...` before making
the required customizations.

* [Mirage2/scripts/person-lookup.js](../dspace-5_x-sesame/dspace/modules/xmlui-mirage2/src/main/webapp/themes/Mirage2/scripts/person-lookup.js)
* [Mirage2/xsl/core/elements.xsl](../dspace-5_x-sesame/dspace/modules/xmlui-mirage2/src/main/webapp/themes/Mirage2/xsl/core/elements.xsl)
* [Mirage2/xsl/core/page-structure.xsl](../dspace-5_x-sesame/dspace/modules/xmlui-mirage2/src/main/webapp/themes/Mirage2/xsl/core/page-structure.xsl)
* [Mirage2/xsl/core/forms.xsl](../dspace-5_x-sesame/dspace/modules/xmlui-mirage2/src/main/webapp/themes/Mirage2/xsl/core/forms.xsl)
* [Mirage2/xsl/aspect/artifactbrowser/item-view.xsl](../dspace-5_x-sesame/dspace/modules/xmlui-mirage2/src/main/webapp/themes/Mirage2/xsl/aspect/artifactbrowser/item-view.xsl)
* [Mirage2/styles/_style.scss](../dspace-5_x-sesame/dspace/modules/xmlui-mirage2/src/main/webapp/themes/Mirage2/xsl/styles/_style.scss)

### [person-lookup.js](../dspace-5_x-sesame/dspace/modules/xmlui-mirage2/src/main/webapp/themes/Mirage2/scripts/person-lookup.js) Changes

By default, ORCID lookup searches using " *lastName* *firstName* ", which returns no results. This needs to be changed
to search using " *firstName* *lastName* "

In the `fnInitComplete` function, modify `initialInput` from:
```js
initialInput = (lastName.val() + " " + $('input[name=' + authorityInput + '_first]').val()).trim();
```
to:
```js
initialInput = ($('input[name=' + authorityInput + '_first]').val() + " " + lastName.val()).trim();
```

### [elements.xsl](../dspace-5_x-sesame/dspace/modules/xmlui-mirage2/src/main/webapp/themes/Mirage2/xsl/core/elements.xsl) Changes

DSpace has a bug which does not properly resubmit a lookup search.
Instead of doing a submit during a lookup, we want to refresh the search form.

In the `match="dri:div[@interactive='yes']"` template, surround the following line:
```xml
<xsl:attribute name="onsubmit">javascript:tSubmit(this);</xsl:attribute>
```
with
```xml
<xsl:template match="dri:div[@interactive='yes']" priority="2">
    ...
+   <!-- If this is a ChoiceLookupTransformer form, do a search instead of submit-->
+   <xsl:choose>
+       <xsl:when test="contains(@id, 'ChoiceLookupTransformer')">
+           <xsl:attribute name="onsubmit">javascript:DSpaceChoicesSearch(this);return false;</xsl:attribute>
+       </xsl:when>
+       <xsl:otherwise>
            <xsl:attribute name="onsubmit">javascript:tSubmit(this);</xsl:attribute>
+       </xsl:otherwise>
+   </xsl:choose>
    ...
</xsl:template>
```

In the `match="dri:list[not(@type)]/dri:item"` template, add the `flex` class attribute to the `<td>` node:
```xml
<xsl:template match="dri:list[not(@type)]/dri:item" priority="2" mode="labeled">
    <tr>
        <xsl:attribute name="class">
            <xsl:text>ds-table-row </xsl:text>
            <xsl:if test="(position() mod 2 = 0)">even </xsl:if>
            <xsl:if test="(position() mod 2 = 1)">odd </xsl:if>
            <xsl:value-of select="@rend"/>
        </xsl:attribute>
        <xsl:if test="name(preceding-sibling::*[position()=1]) = 'label'">
            <xsl:apply-templates select="preceding-sibling::*[position()=1]" mode="labeled"/>
        </xsl:if>
+       <td class="flex">
            <xsl:apply-templates />
        </td>
    </tr>
</xsl:template>
```

###  [page-structure.xsl](../dspace-5_x-sesame/dspace/modules/xmlui-mirage2/src/main/webapp/themes/Mirage2/xsl/core/page-structure.xsl) Changes

Now, we need to make sure that new JavaScript function is available.

In the 'buildHead' template, add the following script:
```xml
<xsl:template name="buildHead">
    ...
    <script>
        ...
+       //This function allows choice-support to search
+       function DSpaceChoicesSearch(form) {
+       var query = $('#aspect_general_ChoiceLookupTransformer_field_text1').val();
+       $('*[name="paramValue"]').val(query);
+
+       DSpaceChoicesLoad(form);
+       }
    </script>
    ...
</xsl:template>
```

### [forms.xsl](../dspace-5_x-sesame/dspace/modules/xmlui-mirage2/src/main/webapp/themes/Mirage2/xsl/core/forms.xsl) Changes

By default, the `lookup` choice presentation hides the *Add* button. In order to get a Lookup *and* Add button to
appear, we need to comment out a short block.

In the `match="dri:field[dri:field/dri:instance | dri:params/@operations]"` template, comment out the the conditional
block as follows:
```xml
<xsl:template match="dri:field[dri:field/dri:instance | dri:params/@operations]" priority="2">
    ...
    <button type="submit" name="{concat('submit_',@n,'_add')}"
            class="pull-right ds-button-field btn btn-default ds-add-button">
        <!-- Make invisible if we have choice-lookup popup that provides its own Add. -->
+       <!--<xsl:if test="dri:params/@choicesPresentation = 'lookup'">
            <xsl:attribute name="style">
                <xsl:text>display:none;</xsl:text>
            </xsl:attribute>
+       </xsl:if>-->
        <i18n:text>xmlui.mirage2.forms.nonCompositeFieldSet.add</i18n:text>
    </button>
    ...
</xsl:template>
```

### [item-view.xsl](../dspace-5_x-sesame/dspace/modules/xmlui-mirage2/src/main/webapp/themes/Mirage2/xsl/aspect/artifactbrowser/item-view.xsl) Changes

In order to add links from authority values to associated concepts, we need to modify `item-view` templates.

In the `itemSummaryView-DIM-authors-entry` template, add the following below the author node
```xml
<xsl:template name="itemSummaryView-DIM-authors-entry">
    <div>
        <xsl:if test="@authority">
            <xsl:attribute name="class"><xsl:text>ds-dc_contributor_author-authority</xsl:text></xsl:attribute>
        </xsl:if>
        <xsl:copy-of select="node()"/>
+       <xsl:choose>
+           <xsl:when test="@authority and @confidence">
+               <td>
+                   <xsl:call-template name="authorityConfidenceIcon">
+                       <xsl:with-param name="confidence" select="./@confidence"/>
+                   </xsl:call-template>
+               </td>
+               <xsl:if test="@confidence='ACCEPTED'">
+                   <td>
+                       <a>
+                           <xsl:attribute name="href">
+                               <xsl:value-of select="concat('/concept/uuid:',./@authority)"/>
+                           </xsl:attribute>
+                           Concept link
+                       </a>
+                   </td>
+               </xsl:if>
+           </xsl:when>
+               <td></td>
+               <td></td>
+           </xsl:otherwise>
+       </xsl:choose>
    </div>
</xsl:template>
```

In the `match="dim:field" mode="itemDetailView-DIM"` template add the following at the bottom, inside the table row (`<tr>`) element:
```xml
<xsl:template match="dim:field" mode="itemDetailView-DIM">
    <tr>
        ...
+       <xsl:choose>
+           <xsl:when test="./@authority and ./@confidence">
+               <td>
+                   <xsl:call-template name="authorityConfidenceIcon">
+                      <xsl:with-param name="confidence" select="./@confidence"/>
+                   </xsl:call-template>
+               </td>
+               <td>
+                   <a>
+                       <xsl:attribute name="href">
+                           <xsl:value-of select="concat('/concept/uuid:',./@authority)"/>
+                       </xsl:attribute>
+                       Concept link
+                   </a>
+               </td>
+           </xsl:when>
+           <xsl:otherwise>
+               <td></td>
+               <td></td>
+           </xsl:otherwise>
+       </xsl:choose>
    </tr>
</xsl:template>
```

### [_style.scss](../dspace-5_x-sesame/dspace/modules/xmlui-mirage2/src/main/webapp/themes/Mirage2/xsl/styles/_style.scss)

Add the following CSS to `_style.scss` to fix EAC forms
```scss
#aspect_authority_scheme_ManageSchemeMain_list_actions td.flex{
    display: flex;
    p{
        padding-left: 5px;
        padding-right: 5px;
    }
    a{
        padding-left: 10px;
        padding-top: 5px;
    }
}
#aspect_authority_concept_ManageConceptMain_list_actions td.flex{
    display: flex;
    p{
        padding-left: 5px;
        padding-right: 5px;
    }
    a{
        padding-left: 10px;
        padding-top: 5px;
    }
}
#aspect_authority_term_ManageTermMain_list_actions td.flex{
    display: flex;
    p{
        padding-left: 5px;
        padding-right: 5px;
    }
    a{
        padding-left: 10px;
        padding-top: 5px;
    }
}
```

## Build Configuration

Changes are necessary to the following files:

* [dspace-parent/pom.xml](../dspace-5_x-sesame/pom.xml)
* [dspace/modules/additions/pom.xml](../dspace-5_x-sesame/dspace/modules/additions/pom.xml)
* [dspace/modules/xmlui/pom.xml](../dspace-5_x-sesame/dspace/modules/xmlui/pom.xml)

### [dspace-parent pom.xml](../dspace-5_x-sesame/pom.xml) Changes

Add the `dspace-aac` profile to support EAC:
```xml
<profile>
    <id>dspace-aac</id>
    <activation>
        <file>
            <exists>dspace-aac/pom.xml</exists>
        </file>
    </activation>
    <modules>
        <module>dspace-aac</module>
    </modules>
</profile>
```
---
Add the `dspace-aac` module to the pre-existing list of `release` modules:
```xml
<!--
   The 'release' profile is used by the 'maven-release-plugin' (see above)
   to actually perform a DSpace software release to Maven central.
 -->
<profile>
    <id>release</id>
    <activation>
        <activeByDefault>false</activeByDefault>
    </activation>
    <!-- Activate all modules *except* for the 'dspace' module,
         as it does not include any Java source code to release. -->
    <modules>
        <module>dspace-api</module>
        <module>dspace-jspui</module>
        <module>dspace-lni</module>
        <module>dspace-oai</module>
        <module>dspace-rdf</module>
        <module>dspace-rest</module>
        <module>dspace-services</module>
        <module>dspace-solr</module>
        <module>dspace-sword</module>
        <module>dspace-swordv2</module>
        <module>dspace-xmlui-mirage2</module>
        <module>dspace-xmlui</module>
+       <module>dspace-aac</module>
    </modules>
</profile>
```
---
Add the following dependencies:
```xml
<dependency>
    <groupId>com.atmire</groupId>
    <artifactId>aac-xmlui</artifactId>
    <version>5.6</version>
    <type>jar</type>
    <classifier>classes</classifier>
</dependency>
<dependency>
    <groupId>com.atmire</groupId>
    <artifactId>aac-xmlui</artifactId>
    <version>5.6</version>
    <type>war</type>
</dependency>
```

### [Additions pom.xml](../dspace-5_x-sesame/dspace/modules/additions/pom.xml) Changes

Add the following dependency:

```xml
<dependency>
    <groupId>com.atmire</groupId>
    <artifactId>aac-api</artifactId>
    <version>${version}</version>
</dependency>
```

###  [XMLUI pom.xml](../dspace-5_x-sesame/dspace/modules/xmlui/pom.xml) Changes

Under the `mirage2-war` profile, add the following overlay to the top of the `overlays` section:
```xml
<profiles>
    ...
    <profile>
        <id>mirage2-war</id>
        ...
            <overlays>
                <!--
                   the priority of overlays is determined here
                   1.) default: anything in the current project has highest
                   2.) anything defined here has precedence in the order defined
                   3.) any war found transitively in the dependencies will be applied
                   next. the order is unpredictable.
                -->
+               <overlay>
+                   <groupId>com.atmire</groupId>
+                   <artifactId>aac-xmlui</artifactId>
+                   <type>war</type>
+                   <excludes>
+                       <exclude>WEB-INF/classes/**</exclude>
+                   </excludes>
+               </overlay>
                ...
            </overlays>
        ...
    </profile>
</profiles>
```
---
Add the following dependencies:
```xml
<dependency>
    <groupId>com.atmire</groupId>
    <artifactId>aac-xmlui</artifactId>
    <version>${version}</version>
    <type>war</type>
</dependency>
<dependency>
    <groupId>com.atmire</groupId>
    <artifactId>aac-xmlui</artifactId>
    <version>${version}</version>
    <type>jar</type>
    <classifier>classes</classifier>
</dependency>
```

## EAC Configuration

Changes are necessary to the following files:

* [dspace/config/dspace.cfg](../dspace-5_x-sesame/dspace/config/dspace.cfg)
* [dspace/config/xmlui.xconf](../dspace-5_x-sesame/dspace/config/xmlui.xconf)
* [dspace/config/modules/rdf.cfg](../dspace-5_x-sesame/dspace/config/modules/rdf.cfg)
* [dspace/config/modules/rdf/metadata-rdf-mapping.ttl](../dspace-5_x-sesame/dspace/config/modules/rdf/metadata-rdf-mapping.ttl)
* [dspace/config/registires/person-types.xml](../dspace-5_x-sesame/dspace/config/registries/person-types.xml)
* [dspace/config/registries/dublin-core-types.xml](../dspace-5_x-sesame/dspace/config/registries/dublin-core-types.xml)
* [dspace/config/input-forms.xml](../dspace-5_x-sesame/dspace/config/input-forms.xml)

### [dspace.cfg](../dspace-5_x-sesame/dspace/config/dspace.cfg) Changes
Turn on RDF content negotiation
```
rdf.contentNegotiation.enable = true
```
Add `authority` and `thesaurus` to the list of consumers if they are not already present:
```
e.g. event.dispatcher.default.consumers = authority, thesaurus, versioning, discovery, eperson, harvester
```
---
Define the `thesaurus` consumer below the `authority` consumer:
```
event.consumer.thesaurus.class = org.dspace.content.authority.AuthorityConceptEventConsumer
event.consumer.thesaurus.filters = Scheme|Concept|Term+Create|Modify|Modify_Metadata|Delete|Remove|Add
```
---
Define the Solr authority core URL:
```
e.g. solr.authority.server=${solr.authority.server}
```
Note: If using this variable, make sure to define `solr.authority.server` in `build.properties` or your
environment specific properties for the build. Typical value is `${solr.server}/authority`.

---
Define the `ChoiceAuthority` plugin:
```
plugin.named.org.dspace.content.authority.ChoiceAuthority = \
     org.dspace.content.authority.SolrAuthority = SolrAuthorAuthority, \
     org.dspace.content.authority.SolrAuthority = SolrSubjectAuthority
```

#### Authority Metadata Field Configuration
For **_each_** authority metadata field, define the following five (5) properties:

---
##### Authority Plugin
```
choices.plugin.<schema>.<element>.<qualifier> = SolrAuthorAuthority | SolrSubjectAuthority
```
*e.g.*
```
choices.plugin.dc.subject.vessel = SolrSubjectAuthority
```
This value must be one of the `ChoiceAuthority` options defined above.

---
##### Authority Presentation
```
choices.presentation.<schema>.<element>.<qualifier> = authorLookup | lookup | suggest | select
```
*e.g.*
```
choices.presentation.dc.subject.vessel = lookup
```
The value must be one of the following:
* **authorLookup** / **lookup** - User enters a proposed value and clicks a button to "look up" choices based
on that value, and present a pop-up window that lets her navigate through choices.
* **suggest** - As the user types in a text-input field, a menu of suggested choices is automatically generated.
It acts like the Google Suggest feature.
* **select** - Puts up a drop-down menu (or multi-pick selection box) of choices using the HTML SELECT widget.

---
##### Authority Closed
```
choices.closed.<schema>.<element>.<qualifier> = true | false
```
*e.g.*
```
choices.closed.dc.subject.vessel = false
```
If set to `true`, choices are restricted to the set of values offered. If set to `false`, values not included
in the choices are allowed.

---
##### Authority Controlled
```
authority.controlled.<schema>.<element>.<qualifier> = true | false
```
*e.g.*
```
authority.controlled.dc.subject.vessel = true
```
To declare a field as authority-controlled, this value must be `true`.

---
##### Authority Required
```
authority.required.<schema>.<element>.<qualifier> = true | false
```
*e.g.*
```
authority.required.dc.subject.vessel = false
```
If set to `true`, the field *must* have an authority key whenever setting a metadata value.

### [xmlui.xconf](../dspace-5_x-sesame/dspace/config/xmlui.xconf) Changes

Add the `Authority` aspect to the list of aspects:
```xml
<aspect name="Authority" path="resource://aspects/Authority/" />
```
---
Add the `Mirage2` theme to the list of themes. The Mirage2 theme is required for EAC functionality:
```xml
<theme name="Mirage 2 Theme" regex=".*" path="Mirage2/" />
```
Note: Comment out Mirage theme if this was previously enabled.

### [rdf.cfg](../dspace-5_x-sesame/dspace/config/modules/rdf.cfg) Changes

Add `AuthorityDSORelationsConverterPlugin` to `converter.plugins`:
```
converter.plugins = org.dspace.rdf.conversion.StaticDSOConverterPlugin, \
                    org.dspace.rdf.conversion.MetadataConverterPlugin, \
                    org.dspace.rdf.conversion.SimpleDSORelationsConverterPlugin, \
                    org.dspace.rdf.conversion.AuthorityDSORelationsConverterPlugin
```
---
Add `SCHEME` and `CONCEPT` to `converter.DSOtypes`:
```
converter.DSOtypes = SITE, COMMUNITY, COLLECTION, ITEM, SCHEME, CONCEPT
```
---
Add new `simplerelations` to list:
```
simplerelations.bitstream2item = http://purl.org/dc/terms/isPartOf,\
                 http://digital-repositories.org/ontologies/dspace/0.1.0#isPartOfItem
```

### [metadata-rdf-mapping.ttl](../dspace-5_x-sesame/dspace/config/modules/rdf/metadata-rdf-mapping.ttl) Changes

For **_each NEW_** authority metadata field, define the following property:
```
:<name>
  dm:metadataName "<schema>.<element>.<qualifier>" ;
  dm:creates [
    dm:subject dm:DSpaceObjectIRI ;
    dm:predicate dc:<predicate> ;
    dm:object dm:DSpaceValue ;
  ];
  .
```
*e.g.*
```
:vessel
  dm:metadataName "dc.subject.vessel" ;
  dm:creates [
    dm:subject dm:DSpaceObjectIRI ;
    dm:predicate dc:subject ;
    dm:object dm:DSpaceValue ;
  ];
  .
```

### Create [person-types.xml](../dspace-5_x-sesame/dspace/config/registries/person-types.xml)

This metadata registry is used by ORCID when creating Concepts:
```xml
<dspace-dc-types>
    <dc-schema>
        <name>person</name>
        <namespace>http://dspace.org/person</namespace>
    </dc-schema>

    <dc-type>
        <schema>person</schema>
        <element>familyName</element>
    </dc-type>

    <dc-type>
        <schema>person</schema>
        <element>givenName</element>
    </dc-type>

    <dc-type>
        <schema>person</schema>
        <element>institution</element>
    </dc-type>

    <dc-type>
        <schema>person</schema>
        <element>email</element>
    </dc-type>

    <dc-type>
        <schema>person</schema>
        <element>orcid</element>
        <qualifier>id</qualifier>
    </dc-type>
</dspace-dc-types>
```

### Additional Configuration

Be sure to define any new "dc" metadata fields in
[dspace/config/registries/dublin-core-types.xml](../dspace-5_x-sesame/dspace/config/registries/dublin-core-types.xml).

*e.g.*
```xml
<dc-type>
    <schema>dc</schema>
    <element>subject</element>
    <qualifier>vessel</qualifier>
    <scope_note></scope_note>
</dc-type>
```
---
Create an input form to be used during submission for any new metadata fields in
[dspace/config/input-forms.xml](../dspace-5_x-sesame/dspace/config/input-forms.xml).

*e.g.*
```xml
<field>
    <dc-schema>dc</dc-schema>
    <dc-element>subject</dc-element>
    <dc-qualifier>vessel</dc-qualifier>
    <!-- An input-type of twobox MUST be marked as repeatable -->
    <repeatable>true</repeatable>
    <label>Vessel Keywords</label>
    <input-type>twobox</input-type>
    <hint>Enter appropriate vessel keywords or phrases below. </hint>
    <required></required>
</field>
```

## Authority Source Configuration

Changes are necessary to the following file:

* [dspace/config/spring/api/orcid-authority-services.xml](../dspace-5_x-sesame/dspace/config/spring/api/orcid-authority-services.xml)

### Initial Changes

Add the following bean:
```xml
<!-- the bean processor interceptor -->
<bean class="org.dspace.servicemanager.spring.DSpaceBeanPostProcessor" />
```
---
Modify the `AuthoritySolrServiceImpl` bean from:
```xml
<bean class="org.dspace.authority.AuthoritySolrServiceImpl" id="org.dspace.authority.AuthoritySearchService"/>
```
*to*
```xml
<bean class="org.dspace.content.authority.SolrAuthorityServiceImpl" id="org.dspace.authority.AuthoritySearchService"/>
```
----
Add the following alias:
```xml
<alias name="org.dspace.authority.AuthoritySearchService"  alias="org.dspace.content.authority.EditableAuthorityIndexingService"/>
```
---
Modify the `DSpaceAuthorityIndexer` bean from:
```xml
<bean id="dspace.DSpaceAuthorityIndexer" class="org.dspace.authority.indexer.DSpaceAuthorityIndexer"/>
```
*to*
```xml
<bean id="dspace.DSpaceAuthorityIndexer" class="org.dspace.authority.indexer.DSpaceAuthorityIndexer" scope="prototype"/>
```
### AuthorityTypes Configuration
Add the following property to the `AuthorityTypes` bean:
```xml
<property name="config">
    <list>
        <bean class="org.dspace.authority.config.AuthorityTypeConfiguration">
            <!--
            AuthorityType that this configuration applies to
             -->
            <property name="type">
                <bean class="org.dspace.authority.AuthorityValue"/>
            </property>
            <!--
            choice select fields that should be exposed by default for any AuthorityValue
                 These map to Concept metadata stored in "otherMetadata" in the AuthorityValue.
            -->
            <property name="choiceSelectFields">
                <map>
                    <!--
                   choice meta to show for authors from Concept meta. Mapped to
                    simple i18n keys for Labeling
                    -->
                    <entry key="first-name" value="person.givenName"/>
                    <entry key="last-name" value="person.familyName"/>
                    <entry key="email" value="person.email"/>
                    <entry key="institution" value="person.institution"/>
                </map>
            </property>

            <!-- Solr field used for search -->
            <property name="searchFieldType">
                <value>full-text</value>
            </property>

            <!-- Solr field used for sort -->
            <property name="sortFieldType">
                <value>value</value>
            </property>
        </bean>
    </list>
</property>
```

Add `SPARQLAuthorityValue` to the `types` property in the `AuthorityTypes` bean:
```xml
<property name="types">
    <list>
        <bean class="org.dspace.authority.orcid.OrcidAuthorityValue"/>
        <bean class="org.dspace.authority.PersonAuthorityValue"/>
+       <bean class="org.dspace.authority.sparql.SPARQLAuthorityValue"/>
    </list>
</property>
```

Add *each* authority field and its associated AuthorityValue (form the list above) to the `fieldDefaults` property
in the `AuthorityTypes` bean:
```xml
<property name="fieldDefaults">
    <map>
        <entry key="<schema>_<element>_<qualifier>">
            <bean class="<AuthorityValue_class>"/>
        </entry>
    </map>
</property>
```
*e.g.*
```xml
<property name="fieldDefaults">
    <map>
        <entry key="dc_contributor_author">
            <bean class="org.dspace.authority.PersonAuthorityValue"/>
        </entry>
        <entry key="dc_subject_vessel">
            <bean class="org.dspace.authority.AuthorityValue"/>
        </entry>
    </map>
</property>
```

For *each* authority field, map the field to the associated source (to be defined below) in the `externalSources` property
in the `AuthroityTypes` bean:
```xml
<property name="externalSources">
    <map>
        <entry key="<schema>_<element>_<qualifier>">
            <ref bean="<sourceName>"/>
        </entry>
    </map>
</property>
```
*e.g.*
```xml
<property name="externalSources">
    <map>
        <entry key="dc_contributor_author">
            <ref bean="OrcidSource"/>
        </entry>
        <entry key="dc_subject_vessel">
            <ref bean="sparqlVessel"/>
        </entry>
    </map>
</property>
```

### ORCID Configuration

In the preconfigured `OrcidSource` bean, add the following property:
```xml
<property name="schemeId" value="${authority.scheme.orcid}"/>
```

### SPARQL Configuration

For *each* SPARQL source, create a source bean:
```xml
<bean name="<sourceName>" class="org.dspace.authority.sparql.SPARQLSource">
    <property name="endpointUrl" value="http://linked.rvdata.us/sparql"/>
    <property name="termCompletionQuery">
        <value>
            Term_Completetion_Query
        </value>
    </property>
    <property name="recordQuery">
        <value>
            Record_Query
        </value>
    </property>
    <property name="schemeId" value="${authority.scheme.vessel}"/>
</bean>
```
e.g.
```xml
    <bean name="sparqlVessel" class="org.dspace.authority.sparql.SPARQLSource">
        <property name="endpointUrl" value="http://linked.rvdata.us/sparql"/>
        <property name="termCompletionQuery">
            <value>
                PREFIX rdfs: &lt;http://www.w3.org/2000/01/rdf-schema#>
                SELECT DISTINCT ?s ?value WHERE {
                ?s a &lt;http://linked.rvdata.us/vocab/resource/class/Vessel> .
                ?s rdfs:label ?value FILTER regex(?value,'^AC_USER_INPUT')  .
                }
                ORDER BY ?value
                LIMIT 100
            </value>
        </property>
        <property name="recordQuery">
            <value>
                PREFIX db: &lt;http://linked.rvdata.us/resource/>
                PREFIX geo: &lt;http://www.w3.org/2003/01/geo/wgs84_pos#>
                PREFIX foaf: &lt;http://xmlns.com/foaf/0.1/>
                PREFIX r2rmodel: &lt;http://voc.rvdata.us/>
                PREFIX sf: &lt;http://www.opengis.net/ont/sf#>
                PREFIX r2r: &lt;http://linked.rvdata.us/vocab/resource/class/>
                PREFIX vcard: &lt;http://www.w3.org/2001/vcard-rdf/3.0#>
                PREFIX dcterms: &lt;http://purl.org/dc/terms/>
                PREFIX gn: &lt;http://www.geonames.org/ontology#>
                PREFIX geosparql: &lt;http://www.opengis.net/ont/geosparql#>
                PREFIX rdfs: &lt;http://www.w3.org/2000/01/rdf-schema#>
                PREFIX d2r: &lt;http://sites.wiwiss.fu-berlin.de/suhl/bizer/d2r-server/config.rdf#>
                PREFIX map: &lt;http://linked.rvdata.us/resource/#>
                PREFIX owl: &lt;http://www.w3.org/2002/07/owl#>
                PREFIX xsd: &lt;http://www.w3.org/2001/XMLSchema#>
                PREFIX rdf: &lt;http://www.w3.org/1999/02/22-rdf-syntax-ns#>
                PREFIX skos: &lt;http://www.w3.org/2004/02/skos/core#>
                SELECT DISTINCT ?s ?p ?o WHERE {
                BIND(&lt;AC_USER_INPUT> as ?s)
                &lt;AC_USER_INPUT> ?p ?o
                }
            </value>
        </property>
        <property name="schemeId" value="${authority.scheme.vessel}"/>
    </bean>
```

### Properties Configuration

In `build.properties` or your environment specific properties, be sure to define the identifiers for the Schemes to be
associated with these AuthoritySources. These can be retrieved in the user interface from the **Manage Scheme** page,
after creating a Scheme.
```
authority.scheme.vessel = be2455c888ce411e95b66ae6ffffca8f
```
Note: A rebuild will be required after changing these values, unless they are directly replaced in the
`orcid-authority-services.xml`` file in the deployed DSpace directory.

## Solr Configuration

Changes are necessary to the following file:

* [dspace/solr/authority/conf/schema.xml](../dspace-5_x-sesame/dspace/solr/authority/conf/schema.xml)

Add the following field in order to support additional fields:
```xml
<fields>
    ...
+    <!-- Authority Metadata ( meta_{schema}_{element}_{qualifier} -->
+    <dynamicField name="*" type="text" multiValued="true" indexed="true" stored="true" required="false"/>
</fields>
```

## Assembly Configuration

Changes are necessary to the following file:

* [dspace/src/main/assembly/assembly.xml](../dspace-5_x-sesame/dspace/src/main/assembly/assembly.xml)

Add the following exclude and include statements to support spring file parameterization.
```xml
<fileSets>
    ...
    <!-- Copy necessary subdirectories to resulting directory -->
    <fileSet>
        <outputDirectory>.</outputDirectory>
        <includes>
            ...
        </includes>
        <!-- Exclude source code & configs (we'll copy configs below) -->
        <excludes>
            <exclude>src</exclude>
            <exclude>config/dspace.cfg</exclude>
            <exclude>config/log4j.properties</exclude>
            <exclude>config/modules/**</exclude>
+           <exclude>config/spring/**</exclude>
        </excludes>
    </fileSet>
    <!-- Copy over all module configs & filter them -->
    <fileSet>
        <outputDirectory>.</outputDirectory>
        <includes>
            <include>config/modules/**</include>
+           <include>config/spring/**</include>
        </includes>
        <filtered>true</filtered>
    </fileSet>
</fileSets>
```