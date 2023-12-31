USE [SLM_APP4SHARE]
GO
/****** Object:  Table [dbo].[ipa_file]    Script Date: 7/7/2023 10:49:42 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ipa_file](
	[id] [bigint] IDENTITY(1,1) PRIMARY KEY NOT NULL,
	[file_uuid] [varchar](255) UNIQUE NOT NULL,
	[app_name] [varchar](255) NULL,
	[app_display_name] [varchar](255) NULL,
	[app_version] [varchar](255) NULL,
	[app_build] [varchar](255) NULL,
	[bundle_id] [varchar](255) NULL,
	[bundle_executable] [varchar](255) NULL,
	[capabilities] [varchar](255) NULL,
	[supported_platforms] [varchar](255) NULL,
	[minimum_version_required] [varchar](255) NULL,
	[device_family] [varchar](255) NULL,
	[provision_app_id_name] [varchar](255) NULL,
	[provision_name] [varchar](255) NULL,
	[provision_platform] [varchar](255) NULL,
	[provision_team_name] [varchar](255) NULL,
	[provision_uuid] [varchar](255) NULL,
	[provision_creation_date] [datetime2](6) NULL,
	[provision_expiration_date] [datetime2](6) NULL,
	[icon_name] [varchar](255) NULL,
	[base64icon] [text] NULL,
	[base64release_notes] [text] NULL,
	[file_size] [bigint] NULL,
    [created_by] [varchar](255) NULL,
    [created_date] [datetime2](6) NULL,
    [last_modified_by] [varchar](255) NULL,
    [last_modified_date] [datetime2](6) NULL
)
GO
