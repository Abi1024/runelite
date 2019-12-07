package net.runelite.client.plugins.aoewarnings;

import java.time.Instant;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Projectile;
import net.runelite.api.coords.LocalPoint;

@Getter(AccessLevel.PACKAGE)
class ProjectileContainer
{
	private Projectile projectile;
	private Instant startTime;
	private AoeProjectileInfo aoeProjectileInfo;
	private int lifetime;
	private int finalTick;
	@Setter(AccessLevel.PACKAGE)
	private LocalPoint targetPoint;

	ProjectileContainer(Projectile projectile, Instant startTime, int lifetime, int finalTick)
	{
		this.projectile = projectile;
		this.startTime = startTime;
		this.targetPoint = null;
		this.aoeProjectileInfo = AoeProjectileInfo.getById(projectile.getId());
		this.lifetime = lifetime;
		this.finalTick = finalTick;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof ProjectileContainer))
		{
			return false;
		}
		ProjectileContainer that = (ProjectileContainer) o;
		return getProjectile().equals(that.getProjectile());
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getProjectile());
	}
}
