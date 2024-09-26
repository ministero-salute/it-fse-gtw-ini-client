/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * Copyright (C) 2023 Ministero della Salute
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package it.finanze.sanita.fse2.ms.iniclient.controller.impl;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBElement;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.config.IniCFG;
import it.finanze.sanita.fse2.ms.iniclient.controller.IIniOperationCTL;
import it.finanze.sanita.fse2.ms.iniclient.dto.DeleteRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.GetMergedMetadatiDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.GetMetadatiReqDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.GetReferenceReqDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.IniResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.JWTTokenDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.MergedMetadatiRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.UpdateRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.GetMergedMetadatiResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.GetMetadatiCrashProgramDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.GetMetadatiCrashProgramResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.GetMetadatiResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.GetReferenceResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.IniTraceResponseDTO;
import it.finanze.sanita.fse2.ms.iniclient.dto.response.LogTraceInfoDTO;
import it.finanze.sanita.fse2.ms.iniclient.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.IniEdsInvocationETY;
import it.finanze.sanita.fse2.ms.iniclient.service.IIniInvocationMockedSRV;
import it.finanze.sanita.fse2.ms.iniclient.service.IIniInvocationSRV;
import it.finanze.sanita.fse2.ms.iniclient.service.IIssuerSRV;
import it.finanze.sanita.fse2.ms.iniclient.utility.JsonUtility;
import it.finanze.sanita.fse2.ms.iniclient.utility.RequestUtility;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ClassificationType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.IdentifiableType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryError;

/**
 * INI Publication controller.
 */
@Slf4j
@RestController
public class IniOperationCTL extends AbstractCTL implements IIniOperationCTL {

	@Autowired
	private IIniInvocationSRV iniInvocationSRV;

	@Autowired
	private IIniInvocationMockedSRV iniMockInvocationSRV;

	@Autowired
	private IIssuerSRV issuserSRV;

	@Autowired
	private IniCFG iniCFG;

	@PostConstruct
	void postConstruct() {
		String uuid = UUID.randomUUID().toString();
		String nomeFile = uuid + "_"+ System.currentTimeMillis();
		for(int i=0; i<100; i++) {
			String workflowInstanceId = "2.16.840.1.113883.2.9.2.120.4.4.97bb3fc5bee3032679f4f07419e04af6375baafa17024527a98ede920c6812ed.3533f3c475^^^^urn:ihe:iti:xdw:2013:workflowInstanceId";
			IniEdsInvocationETY iniETY = iniInvocationSRV.findByWII(workflowInstanceId, ProcessorOperationEnum.PUBLISH, new Date());
			IniResponseDTO res = iniInvocationSRV.publishByWorkflowInstanceId(iniETY, nomeFile);	
		}
	}
	
	public static void main(String[] args) throws IOException {
		File dir = new File("/Users/vincenzoingenito/Desktop/INI_TEST");
		if(dir.isDirectory()) {
			File[] file = dir.listFiles();
			Map<Long,List<Long>> map = new HashMap<>();
			for(File f : file) {
				if(f.getName().contains(".DS")) {
					continue;
				}
				int indexOfChar = f.getName().indexOf("_");
				String longString = f.getName().substring(indexOfChar+1,f.getName().length());
				List<Long> mss = new ArrayList<>();
				List<String> msInFile = Files.readAllLines(f.toPath());
				for(String s : msInFile) {
					s = s.replace("Start time: ", "");
					mss.add(Long.valueOf(s));
				}
				map.put(Long.valueOf(longString), mss);
			}
			
			Long maxMillis = map.keySet().stream().max(Long::compare).orElse(null);
			
			Long start = maxMillis+ 2000;
			Long end = start + 60000;
			int counter = 0;
			for(Entry<Long, List<Long>> el : map.entrySet()){
				for(Long v : el.getValue()) {
					if(v<start || v > end) {
						continue;
					}
					counter++;
				}
			}
			
			double result = (double) counter / 60;
			
			System.out.println("TRANSAZIONI AL SECONDO:"+result);
			
		}
//		List<Long> ms = new ArrayList<>();
//		ms.add(1727256259783L);
//		ms.add(1727256266824L);
//		ms.add(1727256267841L);
//		ms.add(1727256268876L);
//		ms.add(1727256269631L);
//		ms.add(1727256271067L);
//		ms.add(1727256272071L);
//		ms.add(1727256272865L);
//		ms.add(1727256274399L);
//		ms.add(1727256275441L);
//		ms.add(1727256276411L);
//		ms.add(1727256277063L);
//		ms.add(1727256277733L);
//		ms.add(1727256278371L);
//		ms.add(1727256278905L);
//		ms.add(1727256279748L);
//		ms.add(1727256280452L);
//		ms.add(1727256281054L);
//		ms.add(1727256281534L);
//		ms.add(1727256282234L);
//		ms.add(1727256283368L);
//		ms.add(1727256284669L);
//		ms.add(1727256286178L);
//		ms.add(1727256287487L);
//		ms.add(1727256289115L);
//		ms.add(1727256290820L);
//		ms.add(1727256292385L);
//		ms.add(1727256294036L);
//		ms.add(1727256295991L);
//		ms.add(1727256297587L);
//		ms.add(1727256298869L);
//		ms.add(1727256300463L);
//		ms.add(1727256301950L);
//		ms.add(1727256303984L);
//		ms.add(1727256306456L);
//		ms.add(1727256309266L);
//		ms.add(1727256312380L);
//		ms.add(1727256314274L);
//		ms.add(1727256315480L);
//		ms.add(1727256316122L);
//		ms.add(1727256316590L);
//		ms.add(1727256320226L);
//		ms.add(1727256321825L);
//		ms.add(1727256322848L);
//		ms.add(1727256324120L);
//		ms.add(1727256325350L);
//		ms.add(1727256326776L);
//		ms.add(1727256327714L);
//		ms.add(1727256328423L);
//		ms.add(1727256329575L);
//		ms.add(1727256331010L);
//		ms.add(1727256333059L);
//		ms.add(1727256334342L);
//		ms.add(1727256334826L);
//		ms.add(1727256335342L);
//		ms.add(1727256336038L);
//		ms.add(1727256337643L);
//		ms.add(1727256338271L);
//		ms.add(1727256339294L);
//		ms.add(1727256340109L);
//		ms.add(1727256340956L);
//		ms.add(1727256341986L);
//		ms.add(1727256342833L);
//		ms.add(1727256343991L);
//		ms.add(1727256344641L);
//		ms.add(1727256345296L);
//		ms.add(1727256345874L);
//		ms.add(1727256347214L);
//		ms.add(1727256348901L);
//		ms.add(1727256349807L);
//		ms.add(1727256351070L);
//		ms.add(1727256352878L);
//		ms.add(1727256354326L);
//		ms.add(1727256356047L);
//		ms.add(1727256356594L);
//		ms.add(1727256357095L);
//		ms.add(1727256357823L);
//		ms.add(1727256358682L);
//		ms.add(1727256359370L);
//		ms.add(1727256360243L);
//		ms.add(1727256361162L);
//		ms.add(1727256362369L);
//		ms.add(1727256363584L);
//		ms.add(1727256364422L);
//		ms.add(1727256365435L);
//		ms.add(1727256366438L);
//		ms.add(1727256367278L);
//		ms.add(1727256368025L);
//		ms.add(1727256368935L);
//		ms.add(1727256370089L);
//		ms.add(1727256371635L);
//		ms.add(1727256372777L);
//		ms.add(1727256373705L);
//		ms.add(1727256374985L);
//		ms.add(1727256376109L);
//		ms.add(1727256377646L);
//		ms.add(1727256379190L);
//		ms.add(1727256380671L);
//		ms.add(1727256382022L);
//		ms.add(1727256382891L);
//		ms.add(1727256267161L);
//		ms.add(1727256268249L);
//		ms.add(1727256268897L);
//		ms.add(1727256270251L);
//		ms.add(1727256271239L);
//		ms.add(1727256272069L);
//		ms.add(1727256272866L);
//		ms.add(1727256274402L);
//		ms.add(1727256275697L);
//		ms.add(1727256276429L);
//		ms.add(1727256276894L);
//		ms.add(1727256277467L);
//		ms.add(1727256278117L);
//		ms.add(1727256279018L);
//		ms.add(1727256279515L);
//		ms.add(1727256280222L);
//		ms.add(1727256280738L);
//		ms.add(1727256281351L);
//		ms.add(1727256281858L);
//		ms.add(1727256283045L);
//		ms.add(1727256284417L);
//		ms.add(1727256285788L);
//		ms.add(1727256287472L);
//		ms.add(1727256288762L);
//		ms.add(1727256290210L);
//		ms.add(1727256291616L);
//		ms.add(1727256293522L);
//		ms.add(1727256295520L);
//		ms.add(1727256296881L);
//		ms.add(1727256298185L);
//		ms.add(1727256299370L);
//		ms.add(1727256300833L);
//		ms.add(1727256302460L);
//		ms.add(1727256304587L);
//		ms.add(1727256306475L);
//		ms.add(1727256309251L);
//		ms.add(1727256312378L);
//		ms.add(1727256313595L);
//		ms.add(1727256314347L);
//		ms.add(1727256316455L);
//		ms.add(1727256317138L);
//		ms.add(1727256317772L);
//		ms.add(1727256318521L);
//		ms.add(1727256321175L);
//		ms.add(1727256322365L);
//		ms.add(1727256323762L);
//		ms.add(1727256325006L);
//		ms.add(1727256326432L);
//		ms.add(1727256327713L);
//		ms.add(1727256328979L);
//		ms.add(1727256330496L);
//		ms.add(1727256332502L);
//		ms.add(1727256333927L);
//		ms.add(1727256334982L);
//		ms.add(1727256335702L);
//		ms.add(1727256336808L);
//		ms.add(1727256338004L);
//		ms.add(1727256338606L);
//		ms.add(1727256339255L);
//		ms.add(1727256340111L);
//		ms.add(1727256341197L);
//		ms.add(1727256341987L);
//		ms.add(1727256342832L);
//		ms.add(1727256343547L);
//		ms.add(1727256344528L);
//		ms.add(1727256345306L);
//		ms.add(1727256346357L);
//		ms.add(1727256348133L);
//		ms.add(1727256349571L);
//		ms.add(1727256350039L);
//		ms.add(1727256351888L);
//		ms.add(1727256353625L);
//		ms.add(1727256355120L);
//		ms.add(1727256355761L);
//		ms.add(1727256356312L);
//		ms.add(1727256356870L);
//		ms.add(1727256357399L);
//		ms.add(1727256358203L);
//		ms.add(1727256358735L);
//		ms.add(1727256359686L);
//		ms.add(1727256360527L);
//		ms.add(1727256361257L);
//		ms.add(1727256362128L);
//		ms.add(1727256363058L);
//		ms.add(1727256364018L);
//		ms.add(1727256365092L);
//		ms.add(1727256366438L);
//		ms.add(1727256367270L);
//		ms.add(1727256367987L);
//		ms.add(1727256368825L);
//		ms.add(1727256370386L);
//		ms.add(1727256372397L);
//		ms.add(1727256373171L);
//		ms.add(1727256374435L);
//		ms.add(1727256375540L);
//		ms.add(1727256376825L);
//		ms.add(1727256378638L);
//		ms.add(1727256380310L);
//		ms.add(1727256381735L);
//		ms.add(1727256382697L);
//		ms.add(1727256268413L);
//		ms.add(1727256269792L);
//		ms.add(1727256271067L);
//		ms.add(1727256272098L);
//		ms.add(1727256272870L);
//		ms.add(1727256273995L);
//		ms.add(1727256275101L);
//		ms.add(1727256276139L);
//		ms.add(1727256276730L);
//		ms.add(1727256277300L);
//		ms.add(1727256277733L);
//		ms.add(1727256278371L);
//		ms.add(1727256279159L);
//		ms.add(1727256279723L);
//		ms.add(1727256280477L);
//		ms.add(1727256281173L);
//		ms.add(1727256281605L);
//		ms.add(1727256282238L);
//		ms.add(1727256283375L);
//		ms.add(1727256284668L);
//		ms.add(1727256286177L);
//		ms.add(1727256287472L);
//		ms.add(1727256288758L);
//		ms.add(1727256290213L);
//		ms.add(1727256291645L);
//		ms.add(1727256293523L);
//		ms.add(1727256295519L);
//		ms.add(1727256296885L);
//		ms.add(1727256298188L);
//		ms.add(1727256299371L);
//		ms.add(1727256300849L);
//		ms.add(1727256302452L);
//		ms.add(1727256304554L);
//		ms.add(1727256306475L);
//		ms.add(1727256309259L);
//		ms.add(1727256312378L);
//		ms.add(1727256315497L);
//		ms.add(1727256316088L);
//		ms.add(1727256318342L);
//		ms.add(1727256319640L);
//		ms.add(1727256321171L);
//		ms.add(1727256322407L);
//		ms.add(1727256323408L);
//		ms.add(1727256324709L);
//		ms.add(1727256326002L);
//		ms.add(1727256327383L);
//		ms.add(1727256328859L);
//		ms.add(1727256330547L);
//		ms.add(1727256332531L);
//		ms.add(1727256333909L);
//		ms.add(1727256335002L);
//		ms.add(1727256335555L);
//		ms.add(1727256336205L);
//		ms.add(1727256337084L);
//		ms.add(1727256337661L);
//		ms.add(1727256338172L);
//		ms.add(1727256339120L);
//		ms.add(1727256340185L);
//		ms.add(1727256341217L);
//		ms.add(1727256342172L);
//		ms.add(1727256343099L);
//		ms.add(1727256344012L);
//		ms.add(1727256344640L);
//		ms.add(1727256345400L);
//		ms.add(1727256346348L);
//		ms.add(1727256346850L);
//		ms.add(1727256347480L);
//		ms.add(1727256348768L);
//		ms.add(1727256349789L);
//		ms.add(1727256351072L);
//		ms.add(1727256352876L);
//		ms.add(1727256354342L);
//		ms.add(1727256355129L);
//		ms.add(1727256355782L);
//		ms.add(1727256356442L);
//		ms.add(1727256357168L);
//		ms.add(1727256357752L);
//		ms.add(1727256358700L);
//		ms.add(1727256359198L);
//		ms.add(1727256360005L);
//		ms.add(1727256360971L);
//		ms.add(1727256361962L);
//		ms.add(1727256362852L);
//		ms.add(1727256364416L);
//		ms.add(1727256365432L);
//		ms.add(1727256366439L);
//		ms.add(1727256367229L);
//		ms.add(1727256368123L);
//		ms.add(1727256369100L);
//		ms.add(1727256369833L);
//		ms.add(1727256370513L);
//		ms.add(1727256371573L);
//		ms.add(1727256372826L);
//		ms.add(1727256373828L);
//		ms.add(1727256374932L);
//		ms.add(1727256376109L);
//		ms.add(1727256377661L);
//		ms.add(1727256379186L);
//		ms.add(1727256380650L);
//		ms.add(1727256382024L);
//		ms.add(1727256267033L);
//		ms.add(1727256268485L);
//		ms.add(1727256269660L);
//		ms.add(1727256271067L);
//		ms.add(1727256272075L);
//		ms.add(1727256272871L);
//		ms.add(1727256273993L);
//		ms.add(1727256275132L);
//		ms.add(1727256276153L);
//		ms.add(1727256276712L);
//		ms.add(1727256277555L);
//		ms.add(1727256278116L);
//		ms.add(1727256278931L);
//		ms.add(1727256279451L);
//		ms.add(1727256280219L);
//		ms.add(1727256280724L);
//		ms.add(1727256281351L);
//		ms.add(1727256281859L);
//		ms.add(1727256283368L);
//		ms.add(1727256284684L);
//		ms.add(1727256286181L);
//		ms.add(1727256287473L);
//		ms.add(1727256288689L);
//		ms.add(1727256290492L);
//		ms.add(1727256292092L);
//		ms.add(1727256293911L);
//		ms.add(1727256295998L);
//		ms.add(1727256297216L);
//		ms.add(1727256298779L);
//		ms.add(1727256300111L);
//		ms.add(1727256301487L);
//		ms.add(1727256303436L);
//		ms.add(1727256305633L);
//		ms.add(1727256307816L);
//		ms.add(1727256310936L);
//		ms.add(1727256313200L);
//		ms.add(1727256315201L);
//		ms.add(1727256316457L);
//		ms.add(1727256316996L);
//		ms.add(1727256317748L);
//		ms.add(1727256318355L);
//		ms.add(1727256319249L);
//		ms.add(1727256320522L);
//		ms.add(1727256321826L);
//		ms.add(1727256322851L);
//		ms.add(1727256324120L);
//		ms.add(1727256325345L);
//		ms.add(1727256326782L);
//		ms.add(1727256328439L);
//		ms.add(1727256329850L);
//		ms.add(1727256331102L);
//		ms.add(1727256332868L);
//		ms.add(1727256334341L);
//		ms.add(1727256334876L);
//		ms.add(1727256335504L);
//		ms.add(1727256336203L);
//		ms.add(1727256337656L);
//		ms.add(1727256338283L);
//		ms.add(1727256339271L);
//		ms.add(1727256339984L);
//		ms.add(1727256340630L);
//		ms.add(1727256341401L);
//		ms.add(1727256342412L);
//		ms.add(1727256343179L);
//		ms.add(1727256344012L);
//		ms.add(1727256344957L);
//		ms.add(1727256345651L);
//		ms.add(1727256347186L);
//		ms.add(1727256348901L);
//		ms.add(1727256349457L);
//		ms.add(1727256349806L);
//		ms.add(1727256351075L);
//		ms.add(1727256352876L);
//		ms.add(1727256354323L);
//		ms.add(1727256355419L);
//		ms.add(1727256356066L);
//		ms.add(1727256356781L);
//		ms.add(1727256357552L);
//		ms.add(1727256358106L);
//		ms.add(1727256358799L);
//		ms.add(1727256359699L);
//		ms.add(1727256360432L);
//		ms.add(1727256361162L);
//		ms.add(1727256362391L);
//		ms.add(1727256363272L);
//		ms.add(1727256364425L);
//		ms.add(1727256365419L);
//		ms.add(1727256366136L);
//		ms.add(1727256367537L);
//		ms.add(1727256368147L);
//		ms.add(1727256368620L);
//		ms.add(1727256369567L);
//		ms.add(1727256370385L);
//		ms.add(1727256371015L);
//		ms.add(1727256371954L);
//		ms.add(1727256372777L);
//		ms.add(1727256373705L);
//		ms.add(1727256374932L);
//		ms.add(1727256376110L);
//		ms.add(1727256377647L);
//		ms.add(1727256265254L);
//		ms.add(1727256266999L);
//		ms.add(1727256268044L);
//		ms.add(1727256268931L);
//		ms.add(1727256270252L);
//		ms.add(1727256271599L);
//		ms.add(1727256272482L);
//		ms.add(1727256273993L);
//		ms.add(1727256275122L);
//		ms.add(1727256276138L);
//		ms.add(1727256276711L);
//		ms.add(1727256277425L);
//		ms.add(1727256277803L);
//		ms.add(1727256278371L);
//		ms.add(1727256278905L);
//		ms.add(1727256279455L);
//		ms.add(1727256280228L);
//		ms.add(1727256280738L);
//		ms.add(1727256281351L);
//		ms.add(1727256281858L);
//		ms.add(1727256283048L);
//		ms.add(1727256284415L);
//		ms.add(1727256285770L);
//		ms.add(1727256287164L);
//		ms.add(1727256288365L);
//		ms.add(1727256290099L);
//		ms.add(1727256291604L);
//		ms.add(1727256293519L);
//		ms.add(1727256295520L);
//		ms.add(1727256296885L);
//		ms.add(1727256298188L);
//		ms.add(1727256299370L);
//		ms.add(1727256300849L);
//		ms.add(1727256302440L);
//		ms.add(1727256304492L);
//		ms.add(1727256306454L);
//		ms.add(1727256309261L);
//		ms.add(1727256312379L);
//		ms.add(1727256314273L);
//		ms.add(1727256314935L);
//		ms.add(1727256315401L);
//		ms.add(1727256318347L);
//		ms.add(1727256319249L);
//		ms.add(1727256320478L);
//		ms.add(1727256321863L);
//		ms.add(1727256322851L);
//		ms.add(1727256324105L);
//		ms.add(1727256325341L);
//		ms.add(1727256326789L);
//		ms.add(1727256328445L);
//		ms.add(1727256329848L);
//		ms.add(1727256331525L);
//		ms.add(1727256333256L);
//		ms.add(1727256334274L);
//		ms.add(1727256334879L);
//		ms.add(1727256335515L);
//		ms.add(1727256336207L);
//		ms.add(1727256337658L);
//		ms.add(1727256338822L);
//		ms.add(1727256339518L);
//		ms.add(1727256340725L);
//		ms.add(1727256341711L);
//		ms.add(1727256342560L);
//		ms.add(1727256343547L);
//		ms.add(1727256344533L);
//		ms.add(1727256345303L);
//		ms.add(1727256345875L);
//		ms.add(1727256346400L);
//		ms.add(1727256346919L);
//		ms.add(1727256348129L);
//		ms.add(1727256349211L);
//		ms.add(1727256349791L);
//		ms.add(1727256351026L);
//		ms.add(1727256352824L);
//		ms.add(1727256354344L);
//		ms.add(1727256355421L);
//		ms.add(1727256356146L);
//		ms.add(1727256356997L);
//		ms.add(1727256357750L);
//		ms.add(1727256358698L);
//		ms.add(1727256359264L);
//		ms.add(1727256360226L);
//		ms.add(1727256360792L);
//		ms.add(1727256361401L);
//		ms.add(1727256362386L);
//		ms.add(1727256363588L);
//		ms.add(1727256365062L);
//		ms.add(1727256365737L);
//		ms.add(1727256366877L);
//		ms.add(1727256367739L);
//		ms.add(1727256368344L);
//		ms.add(1727256368959L);
//		ms.add(1727256369564L);
//		ms.add(1727256370704L);
//		ms.add(1727256371575L);
//		ms.add(1727256372397L);
//		ms.add(1727256373185L);
//		ms.add(1727256374435L);
//		ms.add(1727256375542L);
//		ms.add(1727256377178L);
//		ms.add(1727256268500L);
//		ms.add(1727256269575L);
//		ms.add(1727256270953L);
//		ms.add(1727256271890L);
//		ms.add(1727256272866L);
//		ms.add(1727256273990L);
//		ms.add(1727256274793L);
//		ms.add(1727256275715L);
//		ms.add(1727256276411L);
//		ms.add(1727256277069L);
//		ms.add(1727256277473L);
//		ms.add(1727256278063L);
//		ms.add(1727256278619L);
//		ms.add(1727256279155L);
//		ms.add(1727256279745L);
//		ms.add(1727256280478L);
//		ms.add(1727256281054L);
//		ms.add(1727256281597L);
//		ms.add(1727256282237L);
//		ms.add(1727256283382L);
//		ms.add(1727256284718L);
//		ms.add(1727256286178L);
//		ms.add(1727256287472L);
//		ms.add(1727256288705L);
//		ms.add(1727256290497L);
//		ms.add(1727256292116L);
//		ms.add(1727256293919L);
//		ms.add(1727256296002L);
//		ms.add(1727256297586L);
//		ms.add(1727256298776L);
//		ms.add(1727256300111L);
//		ms.add(1727256301947L);
//		ms.add(1727256303991L);
//		ms.add(1727256306475L);
//		ms.add(1727256309245L);
//		ms.add(1727256312366L);
//		ms.add(1727256314712L);
//		ms.add(1727256315205L);
//		ms.add(1727256315698L);
//		ms.add(1727256316463L);
//		ms.add(1727256316995L);
//		ms.add(1727256317605L);
//		ms.add(1727256318320L);
//		ms.add(1727256318926L);
//		ms.add(1727256320230L);
//		ms.add(1727256321845L);
//		ms.add(1727256323171L);
//		ms.add(1727256324709L);
//		ms.add(1727256325966L);
//		ms.add(1727256327476L);
//		ms.add(1727256329621L);
//		ms.add(1727256330994L);
//		ms.add(1727256332531L);
//		ms.add(1727256333933L);
//		ms.add(1727256334889L);
//		ms.add(1727256335532L);
//		ms.add(1727256336191L);
//		ms.add(1727256337644L);
//		ms.add(1727256338284L);
//		ms.add(1727256338875L);
//		ms.add(1727256339666L);
//		ms.add(1727256340493L);
//		ms.add(1727256341383L);
//		ms.add(1727256342388L);
//		ms.add(1727256343218L);
//		ms.add(1727256344012L);
//		ms.add(1727256344630L);
//		ms.add(1727256345402L);
//		ms.add(1727256346281L);
//		ms.add(1727256346714L);
//		ms.add(1727256347757L);
//		ms.add(1727256348903L);
//		ms.add(1727256349468L);
//		ms.add(1727256350053L);
//		ms.add(1727256351417L);
//		ms.add(1727256353240L);
//		ms.add(1727256354543L);
//		ms.add(1727256356203L);
//		ms.add(1727256356665L);
//		ms.add(1727256357203L);
//		ms.add(1727256358111L);
//		ms.add(1727256359206L);
//		ms.add(1727256359772L);
//		ms.add(1727256360814L);
//		ms.add(1727256361421L);
//		ms.add(1727256362392L);
//		ms.add(1727256363588L);
//		ms.add(1727256365066L);
//		ms.add(1727256365763L);
//		ms.add(1727256366435L);
//		ms.add(1727256367225L);
//		ms.add(1727256368108L);
//		ms.add(1727256368955L);
//		ms.add(1727256370077L);
//		ms.add(1727256370898L);
//		ms.add(1727256371957L);
//		ms.add(1727256372835L);
//		ms.add(1727256373701L);
//		ms.add(1727256374953L);
//		ms.add(1727256376110L);
//		ms.add(1727256257027L);
//		ms.add(1727256258572L);
//		ms.add(1727256259452L);
//		ms.add(1727256266651L);
//		ms.add(1727256267839L);
//		ms.add(1727256268527L);
//		ms.add(1727256269231L);
//		ms.add(1727256270246L);
//		ms.add(1727256271238L);
//		ms.add(1727256272097L);
//		ms.add(1727256273106L);
//		ms.add(1727256274404L);
//		ms.add(1727256275713L);
//		ms.add(1727256276433L);
//		ms.add(1727256277063L);
//		ms.add(1727256277470L);
//		ms.add(1727256278116L);
//		ms.add(1727256278618L);
//		ms.add(1727256279158L);
//		ms.add(1727256279744L);
//		ms.add(1727256280437L);
//		ms.add(1727256281231L);
//		ms.add(1727256281644L);
//		ms.add(1727256282296L);
//		ms.add(1727256283671L);
//		ms.add(1727256285017L);
//		ms.add(1727256286521L);
//		ms.add(1727256287746L);
//		ms.add(1727256289115L);
//		ms.add(1727256290819L);
//		ms.add(1727256292574L);
//		ms.add(1727256294465L);
//		ms.add(1727256296410L);
//		ms.add(1727256297586L);
//		ms.add(1727256298866L);
//		ms.add(1727256300111L);
//		ms.add(1727256301494L);
//		ms.add(1727256303439L);
//		ms.add(1727256305650L);
//		ms.add(1727256308470L);
//		ms.add(1727256311580L);
//		ms.add(1727256314698L);
//		ms.add(1727256315401L);
//		ms.add(1727256316100L);
//		ms.add(1727256318347L);
//		ms.add(1727256321170L);
//		ms.add(1727256322408L);
//		ms.add(1727256323762L);
//		ms.add(1727256325009L);
//		ms.add(1727256326437L);
//		ms.add(1727256327733L);
//		ms.add(1727256329261L);
//		ms.add(1727256330398L);
//		ms.add(1727256332040L);
//		ms.add(1727256333567L);
//		ms.add(1727256334596L);
//		ms.add(1727256335090L);
//		ms.add(1727256335927L);
//		ms.add(1727256337260L);
//		ms.add(1727256338002L);
//		ms.add(1727256338850L);
//		ms.add(1727256339506L);
//		ms.add(1727256340186L);
//		ms.add(1727256340955L);
//		ms.add(1727256341983L);
//		ms.add(1727256342832L);
//		ms.add(1727256343547L);
//		ms.add(1727256344330L);
//		ms.add(1727256344956L);
//		ms.add(1727256345661L);
//		ms.add(1727256346231L);
//		ms.add(1727256347480L);
//		ms.add(1727256348114L);
//		ms.add(1727256349214L);
//		ms.add(1727256349751L);
//		ms.add(1727256351061L);
//		ms.add(1727256352876L);
//		ms.add(1727256354324L);
//		ms.add(1727256356203L);
//		ms.add(1727256356634L);
//		ms.add(1727256357227L);
//		ms.add(1727256357930L);
//		ms.add(1727256358728L);
//		ms.add(1727256359390L);
//		ms.add(1727256360004L);
//		ms.add(1727256361111L);
//		ms.add(1727256361914L);
//		ms.add(1727256362659L);
//		ms.add(1727256363587L);
//		ms.add(1727256364425L);
//		ms.add(1727256365743L);
//		ms.add(1727256367086L);
//		ms.add(1727256368660L);
//		ms.add(1727256370087L);
//		ms.add(1727256370845L);
//		ms.add(1727256371957L);
//		ms.add(1727256372830L);
//		ms.add(1727256373551L);
//		ms.add(1727256374641L);
//		ms.add(1727256375840L);
//		ms.add(1727256262868L);
//		ms.add(1727256267423L);
//		ms.add(1727256268537L);
//		ms.add(1727256269231L);
//		ms.add(1727256270243L);
//		ms.add(1727256271603L);
//		ms.add(1727256272479L);
//		ms.add(1727256273269L);
//		ms.add(1727256274400L);
//		ms.add(1727256275099L);
//		ms.add(1727256276154L);
//		ms.add(1727256276879L);
//		ms.add(1727256277498L);
//		ms.add(1727256278116L);
//		ms.add(1727256278620L);
//		ms.add(1727256279163L);
//		ms.add(1727256279699L);
//		ms.add(1727256280493L);
//		ms.add(1727256280975L);
//		ms.add(1727256281429L);
//		ms.add(1727256281858L);
//		ms.add(1727256283050L);
//		ms.add(1727256284415L);
//		ms.add(1727256285789L);
//		ms.add(1727256287162L);
//		ms.add(1727256288364L);
//		ms.add(1727256290076L);
//		ms.add(1727256291625L);
//		ms.add(1727256293508L);
//		ms.add(1727256295518L);
//		ms.add(1727256296879L);
//		ms.add(1727256298189L);
//		ms.add(1727256299370L);
//		ms.add(1727256300834L);
//		ms.add(1727256302437L);
//		ms.add(1727256304490L);
//		ms.add(1727256306474L);
//		ms.add(1727256309263L);
//		ms.add(1727256312367L);
//		ms.add(1727256313537L);
//		ms.add(1727256314274L);
//		ms.add(1727256315205L);
//		ms.add(1727256315844L);
//		ms.add(1727256316437L);
//		ms.add(1727256316922L);
//		ms.add(1727256317469L);
//		ms.add(1727256318335L);
//		ms.add(1727256318766L);
//		ms.add(1727256319916L);
//		ms.add(1727256321169L);
//		ms.add(1727256322397L);
//		ms.add(1727256323401L);
//		ms.add(1727256324725L);
//		ms.add(1727256326432L);
//		ms.add(1727256327576L);
//		ms.add(1727256329621L);
//		ms.add(1727256330577L);
//		ms.add(1727256331155L);
//		ms.add(1727256332868L);
//		ms.add(1727256333896L);
//		ms.add(1727256334962L);
//		ms.add(1727256335559L);
//		ms.add(1727256336208L);
//		ms.add(1727256337258L);
//		ms.add(1727256338002L);
//		ms.add(1727256338818L);
//		ms.add(1727256339457L);
//		ms.add(1727256340204L);
//		ms.add(1727256340963L);
//		ms.add(1727256341989L);
//		ms.add(1727256342836L);
//		ms.add(1727256343587L);
//		ms.add(1727256344524L);
//		ms.add(1727256345306L);
//		ms.add(1727256345914L);
//		ms.add(1727256347030L);
//		ms.add(1727256347482L);
//		ms.add(1727256348418L);
//		ms.add(1727256348903L);
//		ms.add(1727256349751L);
//		ms.add(1727256351065L);
//		ms.add(1727256352822L);
//		ms.add(1727256354348L);
//		ms.add(1727256355420L);
//		ms.add(1727256356080L);
//		ms.add(1727256356588L);
//		ms.add(1727256357093L);
//		ms.add(1727256357999L);
//		ms.add(1727256359118L);
//		ms.add(1727256360243L);
//		ms.add(1727256361111L);
//		ms.add(1727256361912L);
//		ms.add(1727256362845L);
//		ms.add(1727256364432L);
//		ms.add(1727256365764L);
//		ms.add(1727256366783L);
//		ms.add(1727256368636L);
//		ms.add(1727256369597L);
//		ms.add(1727256371631L);
//		ms.add(1727256372826L);
//		ms.add(1727256268611L);
//		ms.add(1727256269731L);
//		ms.add(1727256270496L);
//		ms.add(1727256271239L);
//		ms.add(1727256272102L);
//		ms.add(1727256272866L);
//		ms.add(1727256274403L);
//		ms.add(1727256275712L);
//		ms.add(1727256276434L);
//		ms.add(1727256277066L);
//		ms.add(1727256277467L);
//		ms.add(1727256278118L);
//		ms.add(1727256278907L);
//		ms.add(1727256279714L);
//		ms.add(1727256280477L);
//		ms.add(1727256281205L);
//		ms.add(1727256281682L);
//		ms.add(1727256282290L);
//		ms.add(1727256283372L);
//		ms.add(1727256284672L);
//		ms.add(1727256286180L);
//		ms.add(1727256287490L);
//		ms.add(1727256288689L);
//		ms.add(1727256290499L);
//		ms.add(1727256292084L);
//		ms.add(1727256293919L);
//		ms.add(1727256295990L);
//		ms.add(1727256297220L);
//		ms.add(1727256298474L);
//		ms.add(1727256299772L);
//		ms.add(1727256301187L);
//		ms.add(1727256302960L);
//		ms.add(1727256305040L);
//		ms.add(1727256307816L);
//		ms.add(1727256310935L);
//		ms.add(1727256313198L);
//		ms.add(1727256316439L);
//		ms.add(1727256316920L);
//		ms.add(1727256317757L);
//		ms.add(1727256318259L);
//		ms.add(1727256321170L);
//		ms.add(1727256322378L);
//		ms.add(1727256324096L);
//		ms.add(1727256325344L);
//		ms.add(1727256326767L);
//		ms.add(1727256327811L);
//		ms.add(1727256329583L);
//		ms.add(1727256330998L);
//		ms.add(1727256332859L);
//		ms.add(1727256334309L);
//		ms.add(1727256334788L);
//		ms.add(1727256335519L);
//		ms.add(1727256336532L);
//		ms.add(1727256337657L);
//		ms.add(1727256338853L);
//		ms.add(1727256339417L);
//		ms.add(1727256340109L);
//		ms.add(1727256340823L);
//		ms.add(1727256341983L);
//		ms.add(1727256342836L);
//		ms.add(1727256343587L);
//		ms.add(1727256344324L);
//		ms.add(1727256344906L);
//		ms.add(1727256345881L);
//		ms.add(1727256347587L);
//		ms.add(1727256348218L);
//		ms.add(1727256348715L);
//		ms.add(1727256349566L);
//		ms.add(1727256350057L);
//		ms.add(1727256351872L);
//		ms.add(1727256353618L);
//		ms.add(1727256354887L);
//		ms.add(1727256356425L);
//		ms.add(1727256357351L);
//		ms.add(1727256358220L);
//		ms.add(1727256358738L);
//		ms.add(1727256359687L);
//		ms.add(1727256360497L);
//		ms.add(1727256361111L);
//		ms.add(1727256361962L);
//		ms.add(1727256363054L);
//		ms.add(1727256364031L);
//		ms.add(1727256365060L);
//		ms.add(1727256365734L);
//		ms.add(1727256366870L);
//		ms.add(1727256367740L);
//		ms.add(1727256368326L);
//		ms.add(1727256369670L);
//		ms.add(1727256370516L);
//		ms.add(1727256371189L);
//		ms.add(1727256371958L);
//		ms.add(1727256372826L);
//		ms.add(1727256374125L);
//		ms.add(1727256375306L);
//		ms.add(1727256376417L);
//		ms.add(1727256378112L);
//		ms.add(1727256379871L);
//		ms.add(1727256381498L);
//		ms.add(1727256382462L);
//		ms.add(1727256383428L);
//		
//		Long start = 1727256288383L;
//		Long end = 1727256328383L;
//		int count = 0;
//		for(Long m : ms) {
//			if(m<start || m > end) {
//				continue;
//			}
//			count++;
//		}
//		System.out.println(count);
	}
	
	@Override
	public IniTraceResponseDTO create(final String workflowInstanceId, HttpServletRequest request) {
		log.debug("Workflow instance id received:" + workflowInstanceId + ", calling ini invocation client...");
		final LogTraceInfoDTO traceInfoDTO = getLogTraceInfo();

		log.info(Constants.Logs.START_LOG, Constants.Logs.CREATE, Constants.Logs.TRACE_ID_LOG,
				traceInfoDTO.getTraceID(), Constants.Logs.WORKFLOW_INSTANCE_ID, workflowInstanceId);

		IniResponseDTO res = null;
		IniEdsInvocationETY iniETY = iniInvocationSRV.findByWII(workflowInstanceId, ProcessorOperationEnum.PUBLISH, new Date());
		if (!iniCFG.isMockEnable()) {
			res = iniInvocationSRV.publishOrReplaceOnIni(workflowInstanceId, ProcessorOperationEnum.PUBLISH,iniETY);
		} else {
			if (!issuserSRV.isMocked(iniETY.getIssuer())) {
				res = iniInvocationSRV.publishOrReplaceOnIni(workflowInstanceId, ProcessorOperationEnum.PUBLISH,iniETY);
			} else {
				res = iniMockInvocationSRV.publishOrReplaceOnIni(workflowInstanceId, ProcessorOperationEnum.PUBLISH);
			}
		}

		log.info(Constants.Logs.END_LOG, Constants.Logs.CREATE, Constants.Logs.TRACE_ID_LOG, traceInfoDTO.getTraceID(),
				Constants.Logs.WORKFLOW_INSTANCE_ID, workflowInstanceId);

		return new IniTraceResponseDTO(getLogTraceInfo(), res.getEsito(), res.getMessage());
	}

	@Override
	public IniTraceResponseDTO delete(final DeleteRequestDTO requestBody, HttpServletRequest request) {
		log.debug("document id received: " + requestBody.getIdDoc() + ", calling ini delete client...");
		final LogTraceInfoDTO traceInfoDTO = getLogTraceInfo();

		log.info(Constants.Logs.START_LOG, Constants.Logs.DELETE, Constants.Logs.TRACE_ID_LOG,
				traceInfoDTO.getTraceID(), "idDoc", requestBody.getIdDoc());

		IniResponseDTO res = null;
		if (!iniCFG.isMockEnable()) {
			res = iniInvocationSRV.deleteByDocumentId(requestBody);
		} else {
			if (!issuserSRV.isMocked(requestBody.getIss())) {
				res = iniInvocationSRV.deleteByDocumentId(requestBody);
			} else {
				res = iniMockInvocationSRV.deleteByDocumentId(requestBody);
			}
		}

		log.info(Constants.Logs.END_LOG, Constants.Logs.DELETE, Constants.Logs.TRACE_ID_LOG, traceInfoDTO.getTraceID(),
				"idDoc", requestBody.getIdDoc());

		return new IniTraceResponseDTO(getLogTraceInfo(), res.getEsito(), res.getMessage());
	}

	@Override
	public IniTraceResponseDTO update(final UpdateRequestDTO requestBody, HttpServletRequest request) {
		log.debug("Metadata received: {}, calling ini update client...", JsonUtility.objectToJson(requestBody));
		final LogTraceInfoDTO traceInfoDTO = getLogTraceInfo();

		log.info(Constants.Logs.START_UPDATE_LOG, Constants.Logs.UPDATE, Constants.Logs.TRACE_ID_LOG, traceInfoDTO.getTraceID());

		IniResponseDTO res = null;
		SubmitObjectsRequest req = JAXB.unmarshal(new StringReader(requestBody.getMarshallData()), SubmitObjectsRequest.class);
		if (!iniCFG.isMockEnable()) {
			res = iniInvocationSRV.updateByRequestBody(req, requestBody);
		} else {
			if (!issuserSRV.isMocked(requestBody.getToken().getIss())) {
				res = iniInvocationSRV.updateByRequestBody(req, requestBody);
			} else {
				res = iniMockInvocationSRV.updateByRequestBody(req, requestBody);
			}
		}

		log.info(Constants.Logs.END_UPDATE_LOG, Constants.Logs.UPDATE, Constants.Logs.TRACE_ID_LOG, traceInfoDTO.getTraceID());

		return new IniTraceResponseDTO(getLogTraceInfo(), res.getEsito(), res.getMessage());
	}

	@Override
	public IniTraceResponseDTO replace(final String workflowInstanceId, HttpServletRequest request) {
		log.debug("Workflow instance id received replace:" + workflowInstanceId + ", calling ini invocation client...");
		final LogTraceInfoDTO traceInfoDTO = getLogTraceInfo();

		log.info(Constants.Logs.START_LOG, Constants.Logs.REPLACE,
				Constants.Logs.TRACE_ID_LOG, traceInfoDTO.getTraceID(),
				Constants.Logs.WORKFLOW_INSTANCE_ID, workflowInstanceId);

		IniResponseDTO res = null;
		IniEdsInvocationETY iniETY = iniInvocationSRV.findByWII(workflowInstanceId, ProcessorOperationEnum.REPLACE,
				new Date());
		if (!iniCFG.isMockEnable()) {
			res = iniInvocationSRV.publishOrReplaceOnIni(workflowInstanceId, ProcessorOperationEnum.REPLACE,iniETY);
		} else {
			if (!issuserSRV.isMocked(iniETY.getIssuer())) {
				res = iniInvocationSRV.publishOrReplaceOnIni(workflowInstanceId, ProcessorOperationEnum.REPLACE,iniETY);
			} else {
				res = iniMockInvocationSRV.publishOrReplaceOnIni(workflowInstanceId, ProcessorOperationEnum.REPLACE);
			}
		}

		log.info(Constants.Logs.END_LOG, Constants.Logs.REPLACE,
				Constants.Logs.TRACE_ID_LOG, traceInfoDTO.getTraceID(),
				Constants.Logs.WORKFLOW_INSTANCE_ID, workflowInstanceId);

		return new IniTraceResponseDTO(getLogTraceInfo(), res.getEsito(), res.getMessage());
	}

	@Override
	public ResponseEntity<GetMetadatiResponseDTO> getMetadati(String idDoc, GetMetadatiReqDTO req,
			HttpServletRequest request) {
		log.warn(
				"Get metadati - Attenzione il token usato è configurabile dalle properties. Non usare in ambiente di produzione");
		JWTTokenDTO token = new JWTTokenDTO();
		token.setPayload(RequestUtility.buildPayloadFromReq(req));

		GetMetadatiResponseDTO out = new GetMetadatiResponseDTO();
		LogTraceInfoDTO traceInfo = getLogTraceInfo();
		out.setTraceID(traceInfo.getTraceID());
		out.setSpanID(traceInfo.getSpanID());

		if (!iniCFG.isMockEnable()) {
			out.setResponse(iniInvocationSRV.getMetadata(idDoc, token));
		} else {
			if (!issuserSRV.isMocked(req.getIss())) {
				out.setResponse(iniInvocationSRV.getMetadata(idDoc, token));
			} else {
				out.setResponse(iniMockInvocationSRV.getMetadata(idDoc, token));
			}

		}

		return new ResponseEntity<>(out, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<GetReferenceResponseDTO> getReference(String idDoc, GetReferenceReqDTO req,
			HttpServletRequest request) {
		// DELETE
		JWTTokenDTO token = new JWTTokenDTO();
		token.setPayload(RequestUtility.buildPayloadFromReq(req));
		GetReferenceResponseDTO out = null;
		if (!iniCFG.isMockEnable()) {
			out = iniInvocationSRV.getReference(idDoc, token);
		} else {
			if (!issuserSRV.isMocked(req.getIss())) {
				out = iniInvocationSRV.getReference(idDoc, token);
			} else {
				out = iniMockInvocationSRV.getReference(idDoc, token);
			}

		}
		return new ResponseEntity<>(out, HttpStatus.OK);
	}

	@Override
	public GetMergedMetadatiResponseDTO getMergedMetadati(final MergedMetadatiRequestDTO requestBody,
			HttpServletRequest request) {
		log.debug("Call merged metadati");
		GetMergedMetadatiDTO mergedMetadati = null;
		if (!iniCFG.isMockEnable()) {
			mergedMetadati = iniInvocationSRV.getMergedMetadati(requestBody.getIdDoc(), requestBody);
		} else {
			if (!issuserSRV.isMocked(requestBody.getToken().getIss())) {
				mergedMetadati = iniInvocationSRV.getMergedMetadati(requestBody.getIdDoc(), requestBody);
			} else {
				mergedMetadati = iniMockInvocationSRV.getMergedMetadati(requestBody.getIdDoc(), requestBody);
			}
		}

		return new GetMergedMetadatiResponseDTO(getLogTraceInfo(), mergedMetadati.getErrorMessage(),
				mergedMetadati.getMarshallResponse(),
				mergedMetadati.getDocumentType(), mergedMetadati.getAuthorInstitution(),
				mergedMetadati.getAdministrativeRequest());
	}

	@Override
	public ResponseEntity<GetMetadatiCrashProgramResponseDTO> getMetadatiPostCrash(String idDoc, GetMetadatiReqDTO jwtPayload,
			HttpServletRequest request) {
		JWTTokenDTO token = new JWTTokenDTO();
		token.setPayload(RequestUtility.buildPayloadFromReq(jwtPayload));

		boolean documentFound = true;
		AdhocQueryResponse res = iniInvocationSRV.getMetadata(idDoc, token);
		if (res.getRegistryErrorList() != null && !CollectionUtils.isEmpty(res.getRegistryErrorList().getRegistryError())) {
			for(RegistryError error : res.getRegistryErrorList().getRegistryError()) {
				if (error.getCodeContext().equals("No results from the query")) {
					documentFound = false;
					break;
				}
			}
		}
		GetMetadatiCrashProgramDTO metadati = null;
		if(documentFound) {
			metadati = buildFromAdhocQueryRes(res);
		}
		  
		GetMetadatiCrashProgramResponseDTO out = new GetMetadatiCrashProgramResponseDTO();
		out.setMetadati(metadati);
		out.setFoundDocument(documentFound);
		return new ResponseEntity<>(out, HttpStatus.OK);
	}

	  
	private GetMetadatiCrashProgramDTO buildFromAdhocQueryRes(AdhocQueryResponse response) {
		GetMetadatiCrashProgramDTO metadati = new GetMetadatiCrashProgramDTO();
		 
		List<JAXBElement<? extends IdentifiableType>> identifiableList = new ArrayList<>(
				response.getRegistryObjectList().getIdentifiable());
		Optional<JAXBElement<? extends IdentifiableType>> optExtrinsicObject = identifiableList.stream()
				.filter(e -> e.getValue() instanceof ExtrinsicObjectType)
				.findFirst();
		if (optExtrinsicObject.isPresent()) {
			ExtrinsicObjectType extrinsicObject = (ExtrinsicObjectType) optExtrinsicObject.get().getValue();
			for(SlotType1 slot : extrinsicObject.getSlot()){
				
				if("repositoryUniqueId".equals(slot.getName())){
					metadati.setSlotIdentificativoRep(slot.getValueList().getValue().get(0));
				}

				if("urn:ita:2022:administrativeRequest".equals(slot.getName())){
					metadati.setSlotAdministrativeRequest(slot.getValueList().getValue());
				}

				if("serviceStartTime".equals(slot.getName())){
					metadati.setSlotDataInizioPrestazione(slot.getValueList().getValue().get(0));
				}

				if("serviceStopTime".equals(slot.getName())){
					metadati.setSlotDataFinePrestazione(slot.getValueList().getValue().get(0));
				}

				if("urn:ita:2017:repository-type".equals(slot.getName())){
					metadati.setSlotConservazioneANorma(slot.getValueList().getValue().get(0));
				}

				if("urn:ita:2022:urn:ita:2022:description".equals(slot.getName())){
					metadati.setSlotDescriptions(slot.getValueList().getValue());
				}
			}
			for(ClassificationType classificationType : extrinsicObject.getClassification()){
				if("ClassCodeId_1".equals(classificationType.getId())){
					metadati.setClassificationTipoDocumentoLivAlto(classificationType.getNodeRepresentation());
				}

				if("healthcareFacilityTypeCode_1".equals(classificationType.getId())){
					metadati.setClassificationTipologiaStruttura(classificationType.getNodeRepresentation());
				}

				if("practiceSettingCode_1".equals(classificationType.getId())){
					metadati.setClassificationAssettoOrganizzativo(classificationType.getNodeRepresentation());
				}

				if("EventCodeList_1_1".equals(classificationType.getId())){
					metadati.setClassificationAttiCliniciRegoleAccesso(Arrays.asList(classificationType.getNodeRepresentation()));
				}
  
			}

		}
 
		return metadati;
	}
}
