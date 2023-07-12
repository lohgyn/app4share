USE [SLM_APP4SHARE]
GO
    /****** Object:  Table [dbo].[user_role]    Script Date: 7/7/2023 10:49:42 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO 

CREATE TABLE [dbo].[user_role](
    [user_account_id] [bigint] NOT NULL,
    [role_id] [bigint] NOT NULL,
    PRIMARY KEY (user_account_id, role_id),
    FOREIGN KEY (user_account_id) REFERENCES [dbo].[user_account] ([id]),
    FOREIGN KEY (role_id) REFERENCES [dbo].[role] ([id])
)
GO