/*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002 - 2015 Pentaho Corporation (Pentaho). All rights reserved.
 *
 * NOTICE: All information including source code contained herein is, and
 * remains the sole property of Pentaho and its licensors. The intellectual
 * and technical concepts contained herein are proprietary and confidential
 * to, and are trade secrets of Pentaho and may be covered by U.S. and foreign
 * patents, or patents in process, and are protected by trade secret and
 * copyright laws. The receipt or possession of this source code and/or related
 * information does not convey or imply any rights to reproduce, disclose or
 * distribute its contents, or to manufacture, use, or sell anything that it
 * may describe, in whole or in part. Any reproduction, modification, distribution,
 * or public display of this information without the express written authorization
 * from Pentaho is strictly prohibited and in violation of applicable laws and
 * international treaties. Access to the source code contained herein is strictly
 * prohibited to anyone except those individuals and entities who have executed
 * confidentiality and non-disclosure agreements or other agreements with Pentaho,
 * explicitly covering such access.
 */

package com.pentaho.profiling.model;

import com.pentaho.profiling.api.MutableProfileStatus;
import com.pentaho.profiling.api.Profile;
import com.pentaho.profiling.api.ProfileFactory;
import com.pentaho.profiling.api.ProfileStatusManager;
import com.pentaho.profiling.api.ProfileStatusWriteOperation;
import com.pentaho.profiling.api.configuration.DataSourceMetadata;
import com.pentaho.profiling.api.configuration.ProfileConfiguration;
import com.pentaho.profiling.api.configuration.core.AggregateProfileMetadata;
import com.pentaho.profiling.api.metrics.MetricContributorsFactory;

/**
 * Created by bryan on 3/5/15.
 */
public class AggregateProfileFactory implements ProfileFactory {
  private final ProfilingServiceImpl profilingService;
  private final AggregateProfileServiceImpl aggregateProfileService;
  private final MetricContributorsFactory metricContributorsFactory;

  public AggregateProfileFactory( ProfilingServiceImpl profilingService,
                                  AggregateProfileServiceImpl aggregateProfileService,
                                  MetricContributorsFactory metricContributorsFactory ) {
    this.profilingService = profilingService;
    this.aggregateProfileService = aggregateProfileService;
    this.metricContributorsFactory = metricContributorsFactory;
  }

  @Override public boolean accepts( DataSourceMetadata dataSourceMetadata ) {
    return AggregateProfileMetadata.class.isInstance( dataSourceMetadata );
  }

  @Override
  public Profile create( ProfileConfiguration profileConfiguration, ProfileStatusManager profileStatusManager ) {
    AggregateProfileMetadata aggregateProfileMetadata =
      (AggregateProfileMetadata) profileConfiguration.getDataSourceMetadata();
    final String name = aggregateProfileMetadata.getName();
    if ( name != null ) {
      profileStatusManager.write( new ProfileStatusWriteOperation<Void>() {
        @Override public Void write( MutableProfileStatus profileStatus ) {
          profileStatus.setName( name );
          return null;
        }
      } );
    }
    AggregateProfileImpl aggregateProfile =
      new AggregateProfileImpl( profileStatusManager, profilingService, metricContributorsFactory,
        profileConfiguration.getMetricContributors() );
    aggregateProfileService.registerAggregateProfile( aggregateProfile );
    return aggregateProfile;
  }
}
