USE [SLM_APP4SHARE]
GO
    /****** Object:  Table [dbo].[apk_file]    Script Date: 7/7/2023 10:49:42 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO 

CREATE TABLE [dbo].[apk_file](
    [id] [bigint] IDENTITY(1, 1) PRIMARY KEY NOT NULL,
    [file_uuid] [varchar](255) UNIQUE NOT NULL,
    [app_name] [varchar](255) NULL,
    [app_package] [varchar](255) NULL,
    [version_code] [varchar](255) NULL,
    [version_name] [varchar](255) NULL,
    [compile_sdk_version] [varchar](255) NULL,
    [compile_sdk_version_codename] [varchar](255) NULL,
    [platform_build_version_code] [varchar](255) NULL,
    [platform_build_version_name] [varchar](255) NULL,
    [permissions] [text] NULL,
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