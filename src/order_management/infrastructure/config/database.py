"""Database session configuration."""

from collections.abc import AsyncGenerator

from sqlalchemy.ext.asyncio import AsyncSession, async_sessionmaker, create_async_engine

from order_management.infrastructure.adapters.out.persistence.models import Base
from order_management.infrastructure.config.settings import get_settings


class DatabaseSessionProvider:
    """Manages database engine and session factory without mutable global state."""

    _instance: "DatabaseSessionProvider | None" = None

    def __init__(self) -> None:
        self._engine = None
        self._session_factory = None

    @classmethod
    def get_instance(cls) -> "DatabaseSessionProvider":
        """Return the singleton instance."""
        if cls._instance is None:
            cls._instance = cls()
        return cls._instance

    def get_engine(self):
        """Get or create the async SQLAlchemy engine."""
        if self._engine is None:
            settings = get_settings()
            self._engine = create_async_engine(
                settings.database_url,
                echo=False,
            )
        return self._engine

    def get_session_factory(self) -> async_sessionmaker[AsyncSession]:
        """Get the async session factory."""
        if self._session_factory is None:
            engine = self.get_engine()
            self._session_factory = async_sessionmaker(
                bind=engine,
                class_=AsyncSession,
                expire_on_commit=False,
                autoflush=False,
            )
        return self._session_factory


def get_session_provider() -> DatabaseSessionProvider:
    """Return the singleton session provider instance."""
    return DatabaseSessionProvider.get_instance()


def get_engine():
    """Get or create the async SQLAlchemy engine (backward-compatible)."""
    return get_session_provider().get_engine()


def get_session_factory() -> async_sessionmaker[AsyncSession]:
    """Get the async session factory (backward-compatible)."""
    return get_session_provider().get_session_factory()


async def get_db_session() -> AsyncGenerator[AsyncSession, None]:
    """Session generator for dependency injection."""
    factory = get_session_factory()
    async with factory() as session:
        try:
            yield session
            await session.commit()
        except Exception:
            await session.rollback()
            raise
        finally:
            await session.close()
