import Players, { PlayerListType } from '../players/Players';

export const DeadRevealView = (props: { players: PlayerListType }) => {
	return <Players players={props.players} />;
};
