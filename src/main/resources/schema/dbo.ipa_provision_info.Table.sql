USE [SLM_APP4SHARE]
GO
/****** Object:  Table [dbo].[ipa_provision_info]    Script Date: 7/7/2023 10:49:42 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ipa_provision_info](
	[id] [bigint] IDENTITY(1,1) PRIMARY KEY NOT NULL,
	[ipa_file_id] [bigint] NOT NULL,
	[info_key] [varchar](255) NULL,
	[info_value] [text] NULL,
	[info_value_type] [varchar](255) NULL
	FOREIGN KEY([ipa_file_id]) REFERENCES [dbo].[ipa_file] ([id])
)
GO
