import Players, { PlayerList } from '../players/Players';

export const DeadRevealView = (props: { players: PlayerList }) => {
	return <Players players={props.players} />;
};
