"""
Alembic environment configuration.
Carga la URL de la base de datos desde variables de entorno.
"""

from logging.config import fileConfig

from alembic import context
from sqlalchemy import pool
from sqlalchemy.engine import Connection
from sqlalchemy.ext.asyncio import async_engine_from_config
import os
from dotenv import load_dotenv

load_dotenv()

config = context.config
if config.config_file_name is not None:
    fileConfig(config.config_file_name)

# Override sqlalchemy.url from environment
database_url = os.getenv("DATABASE_URL_SYNC", os.getenv("DATABASE_URL", ""))
if database_url and "asyncpg" in database_url:
    database_url = database_url.replace("postgresql+asyncpg", "postgresql")
config.set_main_option("sqlalchemy.url", database_url or "sqlite:///./order_management.db")

from order_management.infrastructure.adapters.out.persistence.models import Base

target_metadata = Base.metadata


def run_migrations_offline() -> None:
    """Run migrations in 'offline' mode."""
    url = config.get_main_option("sqlalchemy.url")
    context.configure(
        url=url,
        target_metadata=target_metadata,
        literal_binds=True,
        dialect_opts={"paramstyle": "named"},
    )

    with context.begin_transaction():
        context.run_migrations()


def run_migrations_online() -> None:
    """Run migrations in 'online' mode."""
    connectable = config.get_main_option("sqlalchemy.url")
    if not connectable:
        connectable = "sqlite:///./order_management.db"

    configuration = config.get_section(config.config_ini_section, {})
    configuration["sqlalchemy.url"] = connectable

    connectable = context.config.get_main_option("sqlalchemy.url")
    if connectable and "postgresql" in connectable:
        connectable = connectable.replace("+asyncpg", "")

    from sqlalchemy import create_engine
    engine = create_engine(connectable, poolclass=pool.NullPool)

    with engine.connect() as connection:
        context.configure(
            connection=connection,
            target_metadata=target_metadata,
        )

        with context.begin_transaction():
            context.run_migrations()


if context.is_offline_mode():
    run_migrations_offline()
else:
    run_migrations_online()
