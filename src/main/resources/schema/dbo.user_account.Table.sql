USE [SLM_APP4SHARE]
GO
    /****** Object:  Table [dbo].[user_account]    Script Date: 7/7/2023 10:49:42 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO 

CREATE TABLE [dbo].[user_account](
    [id] [bigint] IDENTITY(1, 1) PRIMARY KEY NOT NULL,
    [username] [varchar](255) UNIQUE NOT NULL,
    [password] [varchar](255) NULL,
    [account_non_locked] [bit] NULL,
    [created_by] [varchar](255) NULL,
    [created_date] [datetime2](6) NULL,
    [last_modified_by] [varchar](255) NULL,
    [last_modified_date] [datetime2](6) NULL
)
GO